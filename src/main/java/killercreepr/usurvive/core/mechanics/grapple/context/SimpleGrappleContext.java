package killercreepr.usurvive.core.mechanics.grapple.context;

import killercreepr.usurvive.api.mechanics.grapple.context.GrappleContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SimpleGrappleContext implements GrappleContext {
    protected final @NotNull Location location;
    protected final @NotNull Entity owner;
    protected final int id;
    protected final double pullForce;
    protected final double throwForce;
    protected final double pullForceDecayRate;
    protected final double minPullForceMultiplier;
    protected final int decayDuration;

    public SimpleGrappleContext(
        @NotNull Location location, @NotNull Entity owner, int id, double pullForce, double throwForce, double pullForceDecayRate, double minPullForceMultiplier, int decayDuration
    ) {
        this.location = location;
        this.owner = owner;
        this.id = id;
        this.pullForce = pullForce;
        this.throwForce = throwForce;
        this.pullForceDecayRate = pullForceDecayRate;
        this.minPullForceMultiplier = minPullForceMultiplier;
        this.decayDuration = decayDuration;
    }

    @Override
    public double getPullForce() {
        return pullForce;
    }

    @Override
    public double getThrowForce() {
        return throwForce;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public Entity getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    @Override
    public double getPullForceDecayRate() {
        return pullForceDecayRate;
    }

    @Override
    public double getMinPullForceMultiplier() {
        return minPullForceMultiplier;
    }

    @Override
    public int getDecayDuration() {
        return decayDuration;
    }
}
