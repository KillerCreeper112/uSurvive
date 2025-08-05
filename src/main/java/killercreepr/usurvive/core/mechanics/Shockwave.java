package killercreepr.usurvive.core.mechanics;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.math.CruxLocation;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetNear;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateCircle;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Shockwave {
    protected final World world;
    protected final double radius;
    protected final Holder<CruxLocation> center;
    protected final CreateCircle.Type visualType;
    protected final GetNear<Entity> getNear;
    protected final Consumer<Entity> onHit;
    protected final int hitCooldown;

    protected final Map<UUID, Long> hit = new HashMap<>();

    public Shockwave(World world,
                     double radius,
                     Holder<CruxLocation> center,
                     CreateCircle.Type visualType,
                     GetNear<Entity> getNear,
                     Consumer<Entity> onHit,
                     int hitCooldown) {
        this.world = world;
        this.radius = radius;
        this.center = center;
        this.visualType = visualType;
        this.getNear = getNear;
        this.onHit = onHit;
        this.hitCooldown = hitCooldown;
    }

    public boolean wasHitWithin(Entity e, int time){
        Long got = hit.get(e.getUniqueId());
        return got != null && CruxMath.hasOccurredWithin(got, time);
    }

    public void hit(Entity e){
        hit.put(e.getUniqueId(), System.currentTimeMillis());
    }

    public void start(int totalTicks){
        ShapeScheduler.builder()
            .tick(ctx ->{
                int tick = ctx.getTick();
                int max = ctx.getMaxTicks();
                int step = Math.max(1, (int)(max * 0.05));

                if (tick % step != 0) return;
                double range = radius * ctx.getTickProgress();

                getNear.range(range).find().forEach(hit ->{
                    if(wasHitWithin(hit, hitCooldown)) return;
                    onHit.accept(hit);
                    hit(hit);
                });
            })
            .locationTick(ctx ->{
                Location loc = ctx.getLocation().toLocation(world);
                loc.getWorld().spawnParticle(Particle.CRIT, loc, 0);
            })
            .shape(
                CreateCircle.builder()
                    .center(center)
                    .type(visualType)
                    .radius(radius)
                    .spacing(0.5D)
                    .build()
            )
            .buildCached().schedule(totalTicks);
    }
}
