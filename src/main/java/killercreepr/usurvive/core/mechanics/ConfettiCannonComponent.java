package killercreepr.usurvive.core.mechanics;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxTeamComparer;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxattributes.core.component.CruxAttributeComponents;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.combat.EntityHit;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateLine;
import killercreepr.cruxitems.api.item.component.ConsumableComponent;
import killercreepr.cruxitems.api.item.consume.ItemConsumeContext;
import killercreepr.cruxitems.api.item.consume.ItemConsumeResult;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ConfettiCannonComponent implements ConsumableComponent {
    protected final double force;
    protected final double size;
    protected final int parAmount;
    protected final int itemDamage;
    protected final String particle;
    protected final CreateSound sound;
    protected final CreateSound secondSound;
    protected final int secondSoundDelay;
    protected final double minAOE;
    protected final double aoeDropOff;
    protected final float dmgDropOff;
    protected final NumberProvider parLifeSpan;

    public double getSize() {
        return size;
    }

    public double getMinAOE() {
        return minAOE;
    }

    public CreateSound getSecondSound() {
        return secondSound;
    }

    public int getSecondSoundDelay() {
        return secondSoundDelay;
    }

    public float getDmgDropOff() {
        return dmgDropOff;
    }

    public double getAoeDropOff() {
        return aoeDropOff;
    }

    public NumberProvider getParLifeSpan() {
        return parLifeSpan;
    }

    public CreateSound getSound() {
        return sound;
    }

    public ConfettiCannonComponent(double force, double size, int parAmount, int itemDamage, String particle, CreateSound sound, CreateSound secondSound, int secondSoundDelay, double minAOE, double aoeDropOff, float dmgDropOff, NumberProvider parLifeSpan) {
        this.force = force;
        this.size = size;
        this.parAmount = parAmount;
        this.itemDamage = itemDamage;
        this.particle = particle;
        this.sound = sound;
        this.secondSound = secondSound;
        this.secondSoundDelay = secondSoundDelay;
        this.minAOE = minAOE;
        this.aoeDropOff = aoeDropOff;
        this.dmgDropOff = dmgDropOff;
        this.parLifeSpan = parLifeSpan;
    }

    public double getForce() {
        return force;
    }

    public int getParAmount() {
        return parAmount;
    }

    public int getItemDamage() {
        return itemDamage;
    }

    public String getParticle() {
        return particle;
    }

    public void launch(Entity user, Location loc, CruxAttributeContainer attributes){

        Crux.getServer().dispatchCommand(
            Crux.getServer().getConsoleSender(),
            "comet spawn " + particle + " " +
                loc.getWorld().getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() +
                " {\"optimize\":false,\"force\":" + force + ",\"parAmount\":" + parAmount + ",\"pitch\":" + loc.getPitch() + ",\"yaw\":" + loc.getYaw()
                + ",\"lifeSpanMin\":" + parLifeSpan.getMinValue().intValue() + ",\"lifeSpanMax\":" + parLifeSpan.getMaxValue().intValue()
                + "}"
        );
        //"comet spawn confetti ~ ~ ~ ~ {\"optimize\":false,\"force\":2.5,\"yaw\":-179.2,\"pitch\":-19.6,\"parAmount\":300}"
        /*new CometEffectBuilder(particle)
            .location(loc)
            .data(Map.of(
                "optimize", false,
                "force", force,
                "parAmount", parAmount,
                "pitch", loc.getPitch(),
                "yaw", loc.getYaw(),
                "lifeSpanMin", parLifeSpan.getMinValue().intValue(),
                "lifeSpanMax", parLifeSpan.getMaxValue().intValue()
            ))
            .spawn();*/
        if(sound != null) sound.playAt(loc);
        if(secondSound != null){
            if(secondSoundDelay < 1){
                secondSound.playAt(loc);
            }else Crux.scheduler().runTaskLater(() -> secondSound.playAt(loc), secondSoundDelay);
        }

        if(attributes == null) return;

        Collection<UUID> alreadyHit = new HashSet<>();
        CruxTeamComparer teamComparer = CruxTeamComparer.teamComparer(false, team -> !team.allowFriendlyFire());
        new ShotRay(
            loc,
            (int) attributes.getValue(CruxAttribute.ATTACK_RANGE),
            attributes.getValue(CruxAttribute.ATTACK_SPEED),
            attributes.getValueOrDefault(CruxAttribute.ATTACK_AOE, .1D),
            minAOE,
            aoeDropOff,
            attributes,
            e ->{
                return !e.equals(user) && !alreadyHit.contains(e.getUniqueId()) &&
                    !teamComparer.areOnSameTeam(e, user);
            },
            (hit, progress) ->{
                alreadyHit.add(hit.getUniqueId());

                float dmgMultiplier = Math.max(0.2f, 1f - (dmgDropOff * progress));

                EntityDamager.entityDamager(
                    hit, user
                ).attack(
                    attributes.getValue(CruxAttribute.ATTACK_DAMAGE) * dmgMultiplier,
                    attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK),
                    attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK_UP)
                );
            }
        ).start();

        double forceBack = attributes.getValue(CruxAttribute.PUSHBACK);
        double forceBackY = attributes.getValue(CruxAttribute.UPWARDS_PUSHBACK);
        if(forceBack != 0D || forceBackY != 0D){
            Vector force = CruxEntityUtil.getEyeOrLocation(user).getDirection().multiply(-1);
            force.multiply(forceBack);
            if(forceBackY != 0D) force.setY(force.getY()+forceBackY);
            user.setVelocity(user.getVelocity().add(force));
        }
    }

    @Override
    public @NotNull ItemConsumeResult onConsume(@NotNull ItemConsumeContext ctx) {
        Player p = ctx.getPlayer();
        CruxItem crux = ctx.getItem();
        launch(p, p.getEyeLocation(), crux.getOrDefaultData(CruxAttributeComponents.PROJECTILE_CRUX_ATTRIBUTES));
        if(!CruxEntityUtil.isNonSurvival(p)) p.damageItemStack(ctx.getSlot(), itemDamage);

        var useCooldown = crux.item().getData(DataComponentTypes.USE_COOLDOWN);
        if(useCooldown != null){
            p.setCooldown(crux.item(), (int) (useCooldown.seconds() * 20f));
        }

        return ItemConsumeResult.cancelled();
    }

    @Override
    public boolean isConsumable(@NotNull ItemConsumeContext ctx) {
        return true;
    }

    public static class ShotRay{
        protected final Location loc;
        protected final int maxRange;
        protected final double speed;
        protected final double startingAOE;
        protected final double minAOE;
        protected final double aoeDropOff;
        protected final CruxAttributeContainer attributes;
        protected final Predicate<Entity> entityFilter;
        protected final BiConsumer<Entity, Float> entityHit;

        public ShotRay(Location loc, int maxRange, double speed, double startingAOE, double minAOE, double aoeDropOff, CruxAttributeContainer attributes, Predicate<Entity> entityFilter, BiConsumer<Entity, Float> entityHit) {
            this.loc = loc;
            this.maxRange = maxRange;
            this.speed = speed;
            this.startingAOE = startingAOE;
            this.minAOE = minAOE;
            this.aoeDropOff = aoeDropOff;
            this.attributes = attributes;
            this.entityFilter = entityFilter;
            this.entityHit = entityHit;
            this.aoe = startingAOE;
        }

        public RayTraceResult hasHitBlock(EntityHit.Result result){
            for (RayTraceResult ray : result.getResults()) {
                if(ray.getHitBlock() != null) return ray;
            }
            return null;
        }

        public void start(){
            runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    tick();
                }
            };
            runnable.runTaskTimer(Crux.getMainPlugin(), 0L, 1L);
        }

        public double getDmg() {
            return dmg;
        }

        public float getProgress(){
            return (float) range / (float) maxRange;
        }

        protected BukkitRunnable runnable;
        protected int range = 0;
        protected double aoe;
        protected double dmg;
        protected Location lastLoc;
        public void tick(){
            range++;
            if(range >= maxRange){
                stop();
            }

            if(lastLoc == null) lastLoc = loc;
            Location l = CruxLoc.shift(lastLoc, speed, 0D, 0D);

            var result = new EntityHit(
                lastLoc, speed+.05, aoe,
                (int) attributes.getValue(CruxAttribute.ATTACK_PIERCE) + 1,
                false
            ).getHitEntities(entityFilter);

            RayTraceResult hitBlock = hasHitBlock(result);
            if(hitBlock != null){
                stop();
                l = hitBlock.getHitPosition().toLocation(l.getWorld());
            }
            float progress = getProgress();
            result.getHit().forEach(e ->{
                entityHit.accept(e, progress);
            });

            ShapeScheduler.builder()
                .shape(CreateLine.builder()
                    .spacing(1)
                    .start(CruxPosition.precise(lastLoc))
                    .end(CruxPosition.precise(l))
                    .build())
                .locationTick(ctx ->{
                    Location parL = ctx.getLocation().toLocation(loc.getWorld());
                    parL.getWorld().spawnParticle(
                        Particle.FIREWORK, parL, 0
                    );
                })
                .buildCached().scheduleAsync(0);
            lastLoc = l;

            aoe = Math.max(aoe - aoeDropOff, minAOE);
        }

        public void stop(){
            try{
                runnable.cancel();
                runnable = null;
            } catch (NullPointerException | IllegalStateException e) {
            }
        }
    }
}
