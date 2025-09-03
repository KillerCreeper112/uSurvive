package killercreepr.usurvive.core.mechanics.blast_tempest;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.math.CruxLocation;
import killercreepr.crux.core.location.DynamicLocation;
import killercreepr.crux.core.util.*;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.core.component.CruxAttributeComponents;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateCircle;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.entity.memory.SmallTNTMemory;
import killercreepr.usurvive.core.mechanics.BlastTempestComponent;
import killercreepr.usurvive.core.mechanics.StickyChargeComponent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BlastTempestPlayer extends GenericBlastTempestPlayer {
    protected final Holder<Location> lookAt;
    protected final Predicate<Entity> filter;
    protected final double range;
    protected final double yawDegrees;
    protected final double pitchDegrees;
    protected final CreateSound launchSound;
    protected final CreateSound lockOnSound;

    protected boolean invalid = false;
    protected final Map<UUID, LockedRay> gotAlready = new HashMap<>();

    public BlastTempestPlayer(List<BlastTempestComponent.RayData> rays, Consumer<GenericBlastTempestPlayer> onDone,Consumer<GenericBlastTempestPlayer> onFail, Supplier<Boolean> isValid, int consumeTime, Holder<Location> lookAt, Predicate<Entity> filter, double range, double yawDegrees, double pitchDegrees, CreateSound launchSound, CreateSound lockOnSound) {
        super(rays, onDone, onFail, isValid, consumeTime);
        this.lookAt = lookAt;
        this.filter = filter;
        this.range = range;
        this.yawDegrees = yawDegrees;
        this.pitchDegrees = pitchDegrees;
        this.launchSound = launchSound;
        this.lockOnSound = lockOnSound;
    }

    @Override
    public void onDoneInvalid() {
        super.onDoneInvalid();
        setInvalid(true);
    }

    @Override
    public void onDone() {
        super.onDone();
        Location lookAt = this.lookAt.value();
        if(launchSound != null){
            launchSound.playAt(lookAt);
        }

        gotAlready.values().forEach(ray ->{
            double maxRange = ray.rayData.circleRadius * 0.5;
            double minRange = -maxRange;
            Location loc = ray.location.value()
                .clone()
                .add(CruxMath.random(minRange, maxRange), 0, CruxMath.random(minRange, maxRange));

            USurviveMobs.SMALL_TNT.spawn(lookAt, e ->{
                if(ray.rayData.attributes != null){
                    ray.rayData.attributes.getAttributeInstances().forEach(instance ->{
                        instance.getModifiers().forEach(mod ->{
                            CruxAttribute.addModifier(e, instance.getAttribute(), mod);
                        });
                    });
                }

                int fuseTicks = (int) ray.rayData.attributes.getValueOrDefault(
                    CruxAttribute.ATTACK_SPEED, 100D
                );
                CruxTag.set(e, "fuse_ticks", PersistentDataType.INTEGER, fuseTicks);

                if(EntityMemory.get(e) instanceof SmallTNTMemory m){
                    m.setFuseTicks(fuseTicks);
                    m.setData(new StickyChargeComponent(0f, ray.rayData.attributes, fuseTicks, null));
                }

                Vector dir = CruxMath.parabolicMotion(
                    e.getLocation().toVector(), loc.toVector(),
                    (int) ray.rayData.attributes.getValue(CruxAttribute.JUMP_STRENGTH),
                    ray.rayData.attributes.getValueOrDefault(CruxAttribute.GRAVITY, 0.7)
                );
                e.setVelocity(dir);
                CruxEntity crux = CruxEntity.entity(e);
                crux.set(CruxAttributeComponents.CRUX_ATTRIBUTES, ray.rayData.attributes);
            });
        });
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public Collection<Entity> getEntities() {
        Location l = lookAt.value();
        return CruxEntityUtil.filterEntityDistance(
            CruxEntityUtil.getEntitiesInCone(
                l, range, yawDegrees, pitchDegrees,
                e ->{
                    if(gotAlready.containsKey(e.getUniqueId())) return false;
                    return filter == null || filter.test(e);
                }
            ), l, false
        );
    }

    public boolean onLockOnTick(LockedRay ray){
        if(ray.particles != null){
            ray.particles.scheduleAsync(0);
        }
        return false;
    }

    public void lockOn(LockedRay ray){
        if(lockOnSound != null){
            lockOnSound.playAt(ray.location.value());
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        gotAlready.values().removeIf(this::onLockOnTick);
    }

    public ShapeScheduler buildParticles(LockedRay ray){
        return ShapeScheduler.builder()
            .shape(CreateCircle.builder()
                .radius(ray.rayData.circleRadius)
                .center(() ->{
                    Location l = ray.location.value();
                    return CruxLocation.location(l, l.getYaw(), 0f)
                        .add(0, 0.15, 0);
                })
                .spacing(1D)
                .type(CreateCircle.Type.HOLLOW)
                .build())
            .locationTick(ctx ->{
                Location loc = ctx.getLocation().toLocation(ray.location.value().getWorld());
                new ParticleBuilder(Particle.DUST)
                    .extra(0D)
                    .offset(0D, 0D, 0D)
                    .location(loc)
                    .color(Color.RED)
                    .spawn();
            })
            .buildCached();
    }

    @Override
    public boolean onTick(BlastTempestComponent.RayData ray) {
        Collection<Entity> list = getEntities();
        Entity first = CruxCollection.getFirst(list);
        if(first == null) return false;
        LockedRay locked = new LockedRay(ray, ray.homing ? DynamicLocation.createEntity(first) : DynamicLocation.createStatic(first.getLocation()));
        locked.particles = buildParticles(locked);
        gotAlready.put(first.getUniqueId(), locked);
        lockOn(locked);
        return true;
    }

    public static class LockedRay{
        public final BlastTempestComponent.RayData rayData;
        public final Holder<Location> location;
        public ShapeScheduler particles;

        public LockedRay(BlastTempestComponent.RayData rayData, Holder<Location> location) {
            this.rayData = rayData;
            this.location = location;
        }
    }
}
