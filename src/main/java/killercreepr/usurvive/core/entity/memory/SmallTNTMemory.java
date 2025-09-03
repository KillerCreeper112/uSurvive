package killercreepr.usurvive.core.entity.memory;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.entity.memory.DataHolderRegistry;
import killercreepr.crux.core.entity.memory.SimpleEntityMemoryMainThread;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.cruxpotions.core.entity.memory.SimplePotionHolder;
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import killercreepr.usurvive.core.mechanics.StickyChargeComponent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SmallTNTMemory extends SimpleEntityMemoryMainThread {
    protected int fuseTicks;
    public SmallTNTMemory(@NotNull Entity e, DataHolderRegistry dataHolders) {
        super(e, dataHolders);
    }

    public SmallTNTMemory(DataHolderRegistry dataHolders, @NotNull UUID uuid, @NotNull Holder<? extends Entity> holder) {
        super(dataHolders, uuid, holder);
    }

    public SmallTNTMemory(@NotNull Entity e) {
        super(e);
    }

    public SmallTNTMemory(@NotNull UUID uuid, @NotNull Holder<? extends Entity> holder) {
        super(uuid, holder);
    }

    public int getFuseTicks() {
        return fuseTicks;
    }

    public void setFuseTicks(int fuseTicks) {
        this.fuseTicks = fuseTicks;
    }

    protected StickyChargeComponent data;

    public StickyChargeComponent getData() {
        return data;
    }

    public void setData(StickyChargeComponent data) {
        this.data = data;
    }

    public void explode(){
        Entity entity = value();
        double range = CruxAttribute.get(entity, CruxAttribute.ATTACK_RANGE);
        new GetEntityNear<>(Entity.class)
            .filter(e -> !e.equals(value()) && CruxMobGoal.UNDESIRED_BEHAVIOR.test(e))
            .center(entity.getLocation())
            .range(range)
            .find().forEach(hit ->{
                double fallOff = CruxMath.distanceFalloff(hit, entity, range, 1.75f);
                EntityDamager.entityDamager(hit, entity)
                    .attackWithMultiplier(
                        fallOff
                    );

                if(data == null) return;
                if(data.getCruxPotions() != null){
                    PotionHolder holder = EntityMemory.getOrCreateDataHolder(hit, SimplePotionHolder.class,
                        SimplePotionHolder::new);
                    if(holder != null){
                        data.getCruxPotions().forEach(pot ->{
                            int duration = (int) Math.ceil(pot.getDuration() * fallOff);
                            if(duration < 1) return;
                            holder.addPotion(pot.withDuration(duration).create(hit, new EntityInflictor(entity)));
                        });
                    }
                }
            });
        entity.remove();

        new ParticleBuilder(Particle.EXPLOSION)
            .count(CruxMath.random(4,6))
            .location(entity.getLocation())
            .offset(range/3, range/3, range/3)
            .extra(.1)
            .spawn()
            ;
        CreateSound.sound(Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.4f).playAt(entity);
    }

    protected byte fullCooldown = 5;
    protected byte cooldown = 0;
    @Override
    public boolean tick() {
        if(super.tick()) return true;
        onTick();
        return false;
    }

    public byte getFullCooldown() {
        return fullCooldown;
    }

    public void setFullCooldown(byte fullCooldown) {
        this.fullCooldown = fullCooldown;
    }

    public byte getCooldown() {
        return cooldown;
    }

    public void setCooldown(byte cooldown) {
        this.cooldown = cooldown;
    }

    protected byte hitTimes;
    public void onDamage(EntityDamageEvent event){
        if(event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.SUICIDE){
            return;
        }
        if(!(event instanceof EntityDamageByEntityEvent e)){
            event.setCancelled(true);
            return;
        }
        if(CruxEntityUtil.isNonSurvival(e.getDamager())){
            return;
        }
        hitTimes++;
        if(hitTimes > 5){
            hitTimes = 0;
            cooldown = 10;
            reset(event.getEntity());
        }
    }

    public void reset(Entity e){
        hitTimes = 0;
    }

    public void playAnimation(){
        new ModelEntity(value(), CruxMob.getKey(value()).value())
            .playAnimation("explode", 0D, 0D, calculateAnimationSpeed(), true);
    }

    public double calculateAnimationSpeed(){
        return 60D / fuseTicks;
    }

    public void onTick(){
        if(fullCooldown > 0){
            fullCooldown--;
            if(fullCooldown < 1){
                playAnimation();
            }
            return;
        }

        fuseTicks--;
        if(fuseTicks < 1){
            explode();
            //return;
        }
        if(cooldown > 0){
            cooldown--;
        }
    }
}
