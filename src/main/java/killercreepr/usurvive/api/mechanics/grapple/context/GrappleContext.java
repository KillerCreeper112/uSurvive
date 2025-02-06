package killercreepr.usurvive.api.mechanics.grapple.context;

import killercreepr.usurvive.core.mechanics.grapple.context.SimpleGrappleContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface GrappleContext {
    static GrappleContext context(@NotNull Location location, @NotNull Entity owner, int id, double pullForce,
                                  double throwForce,
                                  double pullForceDecayRate, double minPullForceMultiplier, int decayDuration){
        return new SimpleGrappleContext(
            location, owner, id, pullForce, throwForce,
            pullForceDecayRate, minPullForceMultiplier, decayDuration
        );
    }

    double getPullForce();
    double getThrowForce();

    @NotNull
    Location getLocation();

    @NotNull
    Entity getOwner();

    int getId();

    double getPullForceDecayRate();

    double getMinPullForceMultiplier();

    int getDecayDuration();
}
