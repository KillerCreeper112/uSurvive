package killercreepr.usurvive.core.mechanics.whip.active;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.usurvive.core.attribute.USurviveAttribute;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActiveDisplayWhip extends BasicWhip implements Holder<Location> {
    protected @NotNull Location location;
    protected @NotNull Location lastLocation;
    protected final int id;
    protected final int maxDistance;
    protected final double throwSpeed;
    protected final double retractSpeed;
    protected final int pierce;

    protected final @NotNull Display display;
    public ActiveDisplayWhip(@NotNull Location startLocation, int id,
                             int maxDistance,
                             double throwSpeed,
                             double retractSpeed, int pierce,
                             @NotNull Display display) {
        this.id = id;
        this.location = startLocation.clone();
        this.maxDistance = maxDistance;
        this.throwSpeed = throwSpeed;
        this.retractSpeed = retractSpeed;
        this.pierce = pierce;
        this.display = display;
        this.lastLocation = startLocation;
    }

    @Override
    public int getId() {
        return id;
    }
    @Override
    public boolean isValid(@NotNull Entity owner){
        return super.isValid(owner) && owner.getWorld().equals(location.getWorld());
    }

    @Override
    public void remove(@Nullable Entity owner) {
        super.remove(owner);
        Crux.scheduler().runTask(display::remove);
    }

    protected boolean wasCollidingLastTick = false;
    protected boolean retracting = false;
    protected int range = 0;
    @Override
    public void tick(@NotNull Entity owner) {
        if(retracting){
            retractTick(owner);
        }else throwTick(owner);
        updateDisplay(owner, owner.getLocation(), location);
    }

    public Map<UUID, Long> hitEntities(){
        hitEntities.values().removeIf(time -> !CruxMath.hasOccurredWithin(time, 10));
        return hitEntities;
    }

    protected final Map<UUID, Long> hitEntities = new HashMap<>();
    public RayTraceResult rayLogic(@NotNull Entity owner, @NotNull Location location){
        if(hitEntities().size() >= (pierce+1)){
            if(!isRetracting()) setRetracting(true);
            return null;
        }

        double distance = wasCollidingLastTick ? .5D : lastLocation.distanceSquared(location);
        RayTraceResult result = location.getWorld().rayTrace(lastLocation, lastLocation.getDirection(), distance, FluidCollisionMode.NEVER,
            true, .3D, e ->{
                if(e.equals(owner)) return false;
                if(hitEntities.containsKey(e.getUniqueId())) return false;
                return !(e instanceof Display) && !CruxMob.isInCategory(e, MobCategory.OBJECT);
            });
        if(result == null) return null;

        Vector hitPoint = result.getHitPosition();
        location.set(hitPoint.getX(), hitPoint.getY(), hitPoint.getZ());

        // Block hit handling
        if(result.getHitBlock() != null) {
            setRetracting(true);
            return result;
        }

        Entity hit = result.getHitEntity();
        if(hit != null) {
            onEntityHit(owner, hit);
            Location hitLoc = CruxLoc.shiftToward(hitPoint.toLocation(owner.getWorld()), lastLocation, .5D);
            EntityDamager.entityDamager(hit, owner).setHitPosition(hitLoc)
                .attack(
                    CruxAttribute.get(owner, USurviveAttribute.WHIP_DAMAGE),
                    CruxAttribute.get(owner, USurviveAttribute.WHIP_KNOCKBACK),
                    CruxAttribute.get(owner, USurviveAttribute.WHIP_KNOCKBACK_UP)
                );
            hitEntities.put(hit.getUniqueId(), System.currentTimeMillis());
            if(hitEntities.size() >= (pierce+1)) setRetracting(true);
        }
        return result;
    }

    @Override
    public void onEntityHit(Entity owner, Entity hit) {
    }

    public static @NotNull Matrix4f centerPivot(@NotNull Matrix4f addon, @NotNull Vector pivot){
        float x = (float) pivot.getX();
        float y = (float) pivot.getY();
        float z = (float) pivot.getZ();
        Matrix4f center = new Matrix4f().translation(x, y, z);
        // Create a matrix for the reset translation (back to the original position)
        Matrix4f reset = new Matrix4f().translation(x*-1f, y*-1f, z*-1f);

        // Combine the transformations
        Matrix4f combined = new Matrix4f();
        center.mul(addon, combined);
        combined.mul(reset);

        // Now, 'combined' matrix represents the transformation
        // Apply this matrix to your object's vertices or points.
        Vector3f originalPoint = new Vector3f(0, 0, 0);
        Vector3f transformedPoint = new Vector3f();
        combined.transformPosition(originalPoint, transformedPoint);

        return combined;
    }

    public void updateDisplay(Entity owner, Location start, Location end) {
        if(!display.getWorld().equals(owner.getWorld())){
            remove(owner);
            return;
        }
        Vector direction = end.toVector().subtract(start.toVector());
        float distance = (float) direction.length();
        Location tp;
        if(display instanceof BlockDisplay){
            tp = start.clone().add(-.5,.5,-.5);
        }else tp = start.clone().add(0, .8, 0);
        tp.setPitch(0f);
        tp.setYaw(0f);
        display.setInterpolationDelay(0);
        display.setInterpolationDuration(1);
        direction.normalize();
        // Step 2: Convert Direction to Rotation (Look Vector → Quaternion)
        Quaternionf rotation = getQuaternionFromDirection(direction);

        float scale = CruxMath.clamp(distance, 1f, 50f);

        Vector offset = tp.toVector().subtract(display.getLocation().toVector());
        display.setTransformationMatrix(
            new Matrix4f()
                .translate((float) offset.getX(), (float) offset.getY(), (float) offset.getZ())
                .mul(centerPivot(new Matrix4f().rotation(rotation).scale(1, scale, 1),
                    getCenterOffset(display)))
        );
    }

    private Vector getCenterOffset(Display display){
        if(display instanceof BlockDisplay) return new Vector(.5, 0, .5);
        return new Vector(0, -.5, 0);
    }

    private Quaternionf getQuaternionFromDirection(Vector direction) {
        // Forward vector in Minecraft (default facing negative Z)
        Vector forward = new Vector(0, 1, 0);

        // Get the rotation axis and angle needed to rotate forward to target direction
        Vector axis = forward.getCrossProduct(direction).normalize();
        double angle = Math.acos(forward.dot(direction));

        // Convert to JOML Quaternionf
        return new Quaternionf(new AxisAngle4f((float) angle, (float) axis.getX(), (float) axis.getY(), (float) axis.getZ()));
    }


    public void retractTick(@NotNull Entity owner){
        Location ownerLoc = owner.getLocation();
        ownerLoc.add(0D, owner.getHeight()/2D, 0D);

        location = CruxLoc.shiftToward(location, ownerLoc, retractSpeed);

        if(location.distance(ownerLoc) <= (retractSpeed*1.5D)){
            remove(owner);
        }
    }

    public void throwTick(@NotNull Entity owner){
        if(!wasCollidingLastTick){
            lastLocation = location;
            location = CruxLoc.shift(location, throwSpeed, 0D, 0D);
        }

        Crux.scheduler().runTask(() ->{
            if(isScheduledForRemoval()) return;
            RayTraceResult result = rayLogic(owner, location);
            if(result == null){
                wasCollidingLastTick = false;
            }else{
                wasCollidingLastTick = true;
                //if(result.getHitEntity() != null) setRetracting(true);
                lastLocation = location;
                location = result.getHitPosition().toLocation(owner.getWorld());
                location.setDirection(lastLocation.getDirection());
            }
        });
        range++;
        if(range >= maxDistance){
            setRetracting(true);
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

    @NotNull
    public Location getLocation() {
        return location;
    }

    @Override
    public Location value() {
        return location;
    }
}
