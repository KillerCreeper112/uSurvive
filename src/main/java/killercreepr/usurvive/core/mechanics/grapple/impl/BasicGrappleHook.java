package killercreepr.usurvive.core.mechanics.grapple.impl;

import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.usurvive.api.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.core.attribute.USurviveAttribute;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public abstract class BasicGrappleHook implements ActiveGrappleHook {
    protected final double pullForce; //.04
    protected final double pullForceDecayRate;// = 1.0; // Rate at which the pull force decays, higher means faster decay
    protected final double minPullForceMultiplier;// = 0.2; // Minimum value for the pull force multiplier
    protected final int decayDuration;// = 20; // Duration in ticks over which the pull force decays to its minimum value

    public BasicGrappleHook(double pullForce, double pullForceDecayRate, double minPullForceMultiplier, int decayDuration) {
        this.pullForce = pullForce;
        this.pullForceDecayRate = pullForceDecayRate;
        this.minPullForceMultiplier = minPullForceMultiplier;
        this.decayDuration = decayDuration;
    }

    public void onGrappleAttach(){
        if(grappleAttachTime == -1) grappleAttachTime = System.currentTimeMillis();
    }

    public void onEntityHit(Entity owner, Entity hit){
        EntityDamager.entityDamager(
            hit, owner
        ).attack(
            CruxAttribute.get(owner, USurviveAttribute.GRAPPLE_DAMAGE),
            CruxAttribute.get(owner, USurviveAttribute.GRAPPLE_KNOCKBACK),
            CruxAttribute.get(owner, USurviveAttribute.GRAPPLE_KNOCKBACK_UP)
        );
    }

    protected long grappleAttachTime = -1; // -1 means not attached to a block yet
    /*public void pullTowardsTarget(Entity owner, Location target){
        // Calculate how long the grapple has been attached
        long timeAttached = (System.currentTimeMillis() - grappleAttachTime)/50L;

        // Calculate the decay rate based on time spent on the block
        double pullForceMultiplier = Math.max(1.0 - (timeAttached / (float) decayDuration) * pullForceDecayRate, minPullForceMultiplier);

        // Adjust velocity based on the calculated pull force
        Vector vel = target.toVector().subtract(CruxEntityUtil.getEyeOrLocation(owner).toVector())
            .multiply(pullForce * pullForceMultiplier); // Apply the multiplier to the pull force
        double velDistance = vel.length();
        double scaledForce = Math.min(velDistance * 0.1, pullForce * 2);

        vel.normalize().multiply(scaledForce);
        owner.setVelocity(owner.getVelocity().add(vel));
    }
*/

    public void pullTowardsTarget(Entity owner, Location anchor) {
        Location eyeLoc = CruxEntityUtil.getEyeOrLocation(owner);
        Vector toAnchor = anchor.toVector().subtract(eyeLoc.toVector());
        double distance = toAnchor.length();

        final double maxRopeLength = minPullForceMultiplier; //10
        Vector velocity = owner.getVelocity();

        // Gravity simulation
        velocity.setY(velocity.getY() - 0.08);

        // Rope constraint
        if (distance > maxRopeLength) {
            Vector ropeDir = toAnchor.normalize();
            double excess = distance - maxRopeLength;

            // Remove outward movement
            double outwardVel = velocity.dot(ropeDir);
            if (outwardVel > 0) {
                velocity.subtract(ropeDir.multiply(outwardVel));
            }

            // Apply rope tension
            //Vector pullBack = ropeDir.multiply(excess * 0.2);
            double tensionStrength = pullForceDecayRate; // Lower this for gentler pull 0.05
            double clampedExcess = Math.min(excess, maxRopeLength); // Cap the force
            Vector pullBack = ropeDir.multiply(clampedExcess * tensionStrength);

            velocity.add(pullBack);
        }

        // --- Simulated swing force based on input ---
        Vector swingInput = getPlayerInputDirection(owner);
        if (swingInput != null && !swingInput.isZero()) {
            // Tangent to rope arc
            Vector tangent = swingInput.clone().subtract(toAnchor.clone().normalize().multiply(swingInput.dot(toAnchor.clone().normalize())));
            if (!tangent.isZero()) {
                tangent.normalize();
                velocity.add(tangent.multiply(pullForce)); // Swing force 0.1
            }
        }

        owner.setVelocity(velocity);
    }

    protected Vector getPlayerInputDirection(Entity entity) {
        if (!(entity instanceof Player player)) return null;

        // Basic WASD vector (forward/backward only for now)
        Vector input = new Vector(0, 0, 0);
        if (player.isSneaking()) return input; // Optionally block input while sneaking

        if (player.getWalkSpeed() > 0) {
            Location loc = player.getLocation();
            Vector forward = loc.getDirection().setY(0).normalize(); // Ground-level forward
            if (player.isSprinting()) forward.multiply(1.5);

            // TODO: Add actual key tracking for left/right if using custom input API
            input.add(forward); // Assume always pressing forward for now (for demo)
        }

        return input;
    }


    //first gpt
    /*public void pullTowardsTarget(Entity owner, Location target) {
        if (grappleAttachTime == -1) return;

        long timeAttached = (System.currentTimeMillis() - grappleAttachTime) / 50L;

        // Decay multiplier over time
        double pullForceMultiplier = Math.max(
            1.0 - (timeAttached / (float) decayDuration) * pullForceDecayRate,
            minPullForceMultiplier
        );

        // Direction from current location to target
        Vector direction = target.toVector().subtract(CruxEntityUtil.getEyeOrLocation(owner).toVector());

        // Total distance to the hook point
        double distance = direction.length();

        // Normalize direction and apply decayed pull force
        Vector pullVelocity = direction.normalize().multiply(pullForce * pullForceMultiplier);

        // Add a clamping to avoid too aggressive pull at short distances
        double maxSpeed = Math.min(distance * 0.3, pullForce * 2.5); // More aggressive near the start
        if (pullVelocity.length() > maxSpeed) {
            pullVelocity = pullVelocity.normalize().multiply(maxSpeed);
        }

        // Set velocity directly instead of adding (more grappling-hook-like)
        owner.setVelocity(pullVelocity);
    }*/

    public double getPullForce() {
        return pullForce;
    }

    public double getPullForceDecayRate() {
        return pullForceDecayRate;
    }

    public double getMinPullForceMultiplier() {
        return minPullForceMultiplier;
    }

    public int getDecayDuration() {
        return decayDuration;
    }

    public long getGrappleAttachTime() {
        return grappleAttachTime;
    }

    protected boolean scheduledForRemoval = false;

    @Override
    public boolean isScheduledForRemoval() {
        return scheduledForRemoval;
    }

    @Override
    public void scheduleForRemoval() {
        this.scheduledForRemoval = true;
    }

    @Override
    public boolean isValid(@NotNull Entity owner) {
        return !isScheduledForRemoval() && owner.isValid();
    }
}
