package killercreepr.usurvive.mechanics.grapple.impl;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.cruxentities.api.combat.EntityDamager;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ActiveParticleGrappleHook extends BasicGrappleHook implements Holder<Location> {
    protected @NotNull Location location;
    protected final int id;
    protected final double pullForce; //.04
    protected final int maxDistance;
    protected final double throwSpeed;
    protected final double retractSpeed;
    protected final double particleDistance;
    protected final @NotNull ParticleBuilder particle;
    public ActiveParticleGrappleHook(@NotNull Location startLocation, int id, double pullForce,
                                     int maxDistance,
                                     double throwSpeed,
                                     double retractSpeed,
                                     double particleDistance,
                                     @NotNull ParticleBuilder particle) {
        this.id = id;
        this.pullForce = pullForce;
        this.location = startLocation.clone();
        this.maxDistance = maxDistance;
        this.throwSpeed = throwSpeed;
        this.retractSpeed = retractSpeed;
        this.particleDistance = particleDistance;
        this.particle = particle;
    }

    @Override
    public int getId() {
        return id;
    }
    @Override
    public boolean isValid(@NotNull Entity owner){
        return super.isValid(owner) && owner.getWorld().equals(location.getWorld());
    }

    protected boolean wasCollidingLastTick = false;
    protected boolean retracting = false;
    protected int range = 0;
    @Override
    public void tick(@NotNull Entity owner) {
        if(retracting){
            retractTick(owner);
            return;
        }
        throwTick(owner);
    }

    public RayTraceResult rayLogic(@NotNull Entity owner, @NotNull Location location){
        RayTraceResult result = location.getWorld().rayTrace(location, location.getDirection(), .5D, FluidCollisionMode.NEVER,
            true, .3D, e -> !e.equals(owner));
        if(result==null) return null;
        Vector hitPoint = result.getHitPosition();
        location.set(hitPoint.getX(), hitPoint.getY(), hitPoint.getZ());
        if(result.getHitBlock() != null){
            Vector vel = location.toVector().subtract(CruxEntityUtil.getEyeOrLocation(owner).toVector())
                .multiply(pullForce);
            owner.setVelocity(owner.getVelocity().add(vel));
            return result;
        }
        if(result.getHitEntity() != null){
            EntityDamager.entityDamager(result.getHitEntity(), owner).setHitPosition(location).attack(4D, 8D);
        }
        return result;
    }

    public void displayParticles(@NotNull Location previousLocation){
        double distance = location.distance(previousLocation);
        particle.location(previousLocation).spawn();
        while(distance > particleDistance){
            distance -= particleDistance;
            if(distance < 0D) break;
            Location spawn = CruxLoc.shiftToward(previousLocation, location, distance);
            particle.location(spawn).spawn();
        }
        particle.location(location).spawn();
    }

    public void retractTick(@NotNull Entity owner){
        //Location previous = location.clone();
        Location ownerLoc = owner.getLocation();
        ownerLoc.add(0D, owner.getHeight()/2D, 0D);

        location = CruxLoc.shiftToward(location, ownerLoc, retractSpeed);

        displayParticles(ownerLoc);
        if(location.distance(ownerLoc) <= (retractSpeed*1.5D)){
            remove(owner);
        }
    }

    public void throwTick(@NotNull Entity owner){
        if(!wasCollidingLastTick){
            //Location previous = location.clone();
            location = CruxLoc.shift(location, throwSpeed, 0D, 0D);
        }

        Location ownerLoc = owner.getLocation();
        ownerLoc.add(0D, owner.getHeight()/2D, 0D);
        displayParticles(ownerLoc);

        Crux.getServer().getScheduler().runTask(Crux.getMainPlugin(), task ->{
            if(isScheduledForRemoval()) return;
            RayTraceResult result = rayLogic(owner, location);
            if(result == null){
                wasCollidingLastTick = false;
            }else{
                wasCollidingLastTick = true;
                if(result.getHitEntity() != null) setRetracting(true);
            }
        });
        range++;
        if(range >= maxDistance){
            if(!wasCollidingLastTick) setRetracting(true);
        }
    }

    public boolean isRetracting() {
        return retracting;
    }

    public void setRetracting(boolean retracting) {
        this.retracting = retracting;
    }

    public boolean isWasCollidingLastTick() {
        return wasCollidingLastTick;
    }

    public void setWasCollidingLastTick(boolean wasCollidingLastTick) {
        this.wasCollidingLastTick = wasCollidingLastTick;
    }

    public double getRetractSpeed() {
        return retractSpeed;
    }

    public double getThrowSpeed() {
        return throwSpeed;
    }

    public double getPullForce() {
        return pullForce;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @Override
    public Location value() {
        return location;
    }
}
