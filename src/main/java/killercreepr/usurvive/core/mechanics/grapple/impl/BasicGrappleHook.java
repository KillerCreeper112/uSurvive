package killercreepr.usurvive.core.mechanics.grapple.impl;

import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.usurvive.api.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.core.attribute.USurviveAttribute;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
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
    public void pullTowardsTarget(Entity owner, Location target){
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
