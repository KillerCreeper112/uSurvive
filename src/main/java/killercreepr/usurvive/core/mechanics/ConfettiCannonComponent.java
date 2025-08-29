package killercreepr.usurvive.core.mechanics;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxLoc;
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
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConfettiCannonComponent implements ConsumableComponent {
    protected final double force;
    protected final double size;
    protected final int parAmount;
    protected final String particle;
    protected final CreateSound sound;

    public double getSize() {
        return size;
    }

    public CreateSound getSound() {
        return sound;
    }

    public ConfettiCannonComponent(double force, double size, int parAmount, String particle, CreateSound sound) {
        this.force = force;
        this.size = size;
        this.parAmount = parAmount;
        this.particle = particle;
        this.sound = sound;
    }

    public double getForce() {
        return force;
    }

    public int getParAmount() {
        return parAmount;
    }

    public String getParticle() {
        return particle;
    }

    public void launch(Entity user, Location loc, CruxAttributeContainer attributes){

        Crux.getServer().dispatchCommand(
            Crux.getServer().getConsoleSender(),
            "comet spawn " + particle + " " +
                loc.getWorld().getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() +
                " {\"optimize\":false,\"force\":" + force + ",\"parAmount\":" + parAmount + ",\"pitch\":" + loc.getPitch() + ",\"yaw\":" + loc.getYaw() + "}"
        );
        //"comet spawn confetti ~ ~ ~ ~ {\"optimize\":false,\"force\":2.5,\"yaw\":-179.2,\"pitch\":-19.6,\"parAmount\":300}"
        /*new CometEffectBuilder(particle)
            .location(loc)
            .data(Map.of(
                "optimize", false,
                "force", force,
                "parAmount", parAmount,
                "pitch", loc.getPitch(),
                "yaw", loc.getYaw()
            ))
            .size(size).spawn();*/
        if(sound != null) sound.playAt(loc);

        if(attributes == null) return;
        new ShotRay(
            loc,
            (int) attributes.getValue(CruxAttribute.ATTACK_RANGE),
            attributes.getValue(CruxAttribute.ATTACK_SPEED),
            attributes,
            e -> !e.equals(user),
            hit ->{
                EntityDamager.entityDamager(
                    hit, user
                ).attack(
                    attributes.getValue(CruxAttribute.ATTACK_DAMAGE),
                    attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK),
                    attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK_UP)
                );
            }
        ).start();
    }

    @Override
    public @NotNull ItemConsumeResult onConsume(@NotNull ItemConsumeContext ctx) {
        Player p = ctx.getPlayer();
        CruxItem crux = ctx.getItem();
        launch(p, p.getEyeLocation(), crux.getOrDefaultData(CruxAttributeComponents.PROJECTILE_CRUX_ATTRIBUTES));
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
        protected final CruxAttributeContainer attributes;
        protected final Predicate<Entity> entityFilter;
        protected final Consumer<Entity> entityHit;

        public ShotRay(Location loc, int maxRange, double speed, CruxAttributeContainer attributes, Predicate<Entity> entityFilter, Consumer<Entity> entityHit) {
            this.loc = loc;
            this.maxRange = maxRange;
            this.speed = speed;
            this.attributes = attributes;
            this.entityFilter = entityFilter;
            this.entityHit = entityHit;
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

        protected BukkitRunnable runnable;
        protected int range = 0;
        protected Location lastLoc;
        public void tick(){
            range++;
            if(range >= maxRange){
                stop();
            }

            if(lastLoc == null) lastLoc = loc;
            Location l = CruxLoc.shift(lastLoc, speed, 0D, 0D);

            var result = new EntityHit(
                lastLoc, speed, attributes.getValueOrDefault(CruxAttribute.ATTACK_AOE, .1D),
                (int) attributes.getValue(CruxAttribute.ATTACK_PIERCE) + 1,
                false
            ).getHitEntities(entityFilter);

            RayTraceResult hitBlock = hasHitBlock(result);
            if(hitBlock != null){
                stop();
                l = hitBlock.getHitPosition().toLocation(l.getWorld());
            }

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
