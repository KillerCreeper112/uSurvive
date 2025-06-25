package killercreepr.usurvive.core.potion;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.crux.core.util.GetNear;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.entity.memory.SimplePotionHolder;
import killercreepr.cruxpotions.core.potions.ActiveEntityPotion;
import killercreepr.usurvive.core.game.SporelinkVisualizer;
import killercreepr.usurvive.core.lang.Lang;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class SporelinkPotion extends ActiveEntityPotion<LivingEntity> {
    public SporelinkPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(LivingEntity.class, potion, entity, duration, amplifier);
    }

    public static SporelinkVisualizer getOrCreateVisualizer(){
        if(CruxRegistries.TICK.get(SporelinkVisualizer.KEY) instanceof SporelinkVisualizer v){
            return v;
        }
        SporelinkVisualizer visualizer = new SporelinkVisualizer(300, PARTICLE_TICK_RATE-2);
        CruxRegistries.TICK.register(visualizer);
        return visualizer;
    }

    public static final int PARTICLE_TICK_RATE = 20;

    public void particleTick(){
        SporelinkVisualizer visualizer = getOrCreateVisualizer();
        visualizer.setLastAccessed(visualizer.getTick());
        Crux.scheduler().runTaskMain(() ->{
            visualizer.setLink(entity, this);
            lastLinked = getSporeLinked();
            /*getSporeLinked().forEach(linked ->{
                ShapeScheduler.builder()
                    .locationTick(ctx ->{
                        new ParticleBuilder(Particle.ELECTRIC_SPARK)
                            .location(ctx.getLocation().toLocation(entity.getWorld()))
                            .offset(0, 0, 0)
                            .extra(0)
                            .spawn();
                    })
                    .shape(CreateWarpedLine.builder()
                        .start(() -> CruxLocation.location(entity.getLocation().add(0, entity.getHeight()/2, 0)))
                        .end(() -> CruxLocation.location(linked.getLocation().add(0, linked.getHeight()/2, 0)))
                        .spacing(0.5D)
                        .warpStrength(0.5D)
                        .tickOffset(NumberProvider.holder(() ->{
                            return (System.currentTimeMillis() / 50L) % 10000;
                        }))
                        .build())
                    .build().scheduleAsync(0);
            });*/
        });

        new ParticleBuilder(Particle.WHITE_ASH)
            .location(entity.getLocation().add(0, entity.getHeight()/2, 0))
            .count(CruxMath.random(5, 7))
            .offset(.5, .5, .5)
            .extra(.1)
            .spawn();
    }

    public boolean isValidLink(Entity e, boolean rangeCheck){
        if(e.equals(entity)) return false;
        if(!(CruxEntityUtil.isValid(e) && e.getWorld().equals(entity.getWorld()))) return false;

        PotionHolder holder = EntityMemory.getDataHolder(e, SimplePotionHolder.class);
        if(holder == null) return false;
        if(!(holder.getPotion(potion) instanceof SporelinkPotion pot)) return false;
        if(pot.isOnCooldown()) return false;

        if(entity instanceof LivingEntity ee && !ee.hasLineOfSight(e)) return false;
        if(!rangeCheck) return true;
        double range = getEffectiveRadius();
        return e.getLocation().distanceSquared(entity.getLocation()) <= (range*range);
    }

    protected Collection<Entity> lastLinked = List.of();

    protected int cooldown = 0;
    protected long lastMsg;

    public boolean attemptMsg(){
        if(CruxMath.hasOccurredWithin(lastMsg, 600)) return false;
        lastMsg = System.currentTimeMillis();
        Lang.SPORELINK_DAMAGED.use(entity, TagContainer.merged().hook(potion));
        return true;
    }

    public boolean isOnCooldown(){
        return cooldown > 0;
    }

    public Collection<Entity> getSporeLinked(){
        return new GetEntityNear<>(Entity.class)
            .center(entity)
            .range(getEffectiveRadius())
            .amount(getMaxLinkAmount())
            .operation(GetNear.Operation.NEAREST)
            .filter(check -> isValidLink(check, false))
            .find();
    }

    public void onSporeLinkDamage(Entity fromEntity){
        setCooldown(getEffectiveCooldown());
        attemptMsg();

        new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
            .location(entity.getLocation().add(0, entity.getHeight()/2, 0))
            .count(CruxMath.random(5, 7))
            .offset(.5, .5, .5)
            .extra(.1)
            .colorTransition(Color.fromRGB(0xF2F280), Color.fromRGB(0x836A7D),
                CruxMath.random(0.7f, 0.9f))
            .spawn();
    }

    public void onTakeDamage(EntityDamageEvent event){
        if(isOnCooldown()) return;
        setCooldown(getEffectiveCooldown());
        var source = event.getDamageSource();
        getSporeLinked().forEach(hit ->{
            PotionHolder holder = EntityMemory.getDataHolder(hit, SimplePotionHolder.class);
            if(holder == null) return;
            if(holder.getPotion(potion) instanceof SporelinkPotion pot) pot.onSporeLinkDamage(event.getEntity());
            EntityDamager.entityDamager(hit, source.getDirectEntity())
                .setSource(source)
                .setCause(CruxEntityDamageEvent.DamageCause.fromBukkit(event.getCause()))
                .attack(event.getDamage());
        });
    }

    public void onTakeDamage(CruxEntityDamageEvent event){
        if(isOnCooldown()) return;
        setCooldown(getEffectiveCooldown());
        var source = event.getSource();
        getSporeLinked().forEach(hit ->{
            PotionHolder holder = EntityMemory.getDataHolder(hit, SimplePotionHolder.class);
            if(holder == null) return;
            if(holder.getPotion(potion) instanceof SporelinkPotion pot) pot.onSporeLinkDamage(event.getEntity());
            EntityDamager.entityDamager(hit, source.getDirectEntity())
                .setSource(source)
                .setHitPosition(event.getHitPosition())
                .setCause(event.getCause())
                .attack(event.getTrueDmg(), event.getTrueKb(), event.getTrueUpKb());
        });
    }

    public long getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(long lastMsg) {
        this.lastMsg = lastMsg;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    protected void onTick() {
        super.onTick();
        if(cooldown > 0) cooldown--;

        SporelinkVisualizer visualizer = getOrCreateVisualizer();

        if(visualizer.getTick() % PARTICLE_TICK_RATE == 0){
            particleTick();
        }
    }

    public double getEffectiveRadius(){
        return 12D + ((amplifier)*6D);
    }

    public int getEffectiveCooldown(){
        return Math.max(5, 80 - (amplifier * 10));
    }

    public int getMaxLinkAmount(){
        return 6 + (amplifier * 5);
    }
}
