package killercreepr.usurvive.core.mechanics.grapple.context;

import killercreepr.usurvive.api.mechanics.grapple.context.DisplayGrappleContext;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DisplaySimpleGrappleContext extends SimpleGrappleContext implements DisplayGrappleContext {
    protected final Key typeKey;
    protected final Class<? extends Display> displayType;
    protected final double pullForce;
    protected final int maxDistance;
    protected final double throwSpeed;
    protected final double retractSpeed;
    public DisplaySimpleGrappleContext(@NotNull Location location, @NotNull Entity owner, int id, double pullForce, double throwForce, Key typeKey, Class<? extends Display> displayType, int maxDistance, double throwSpeed, double retractSpeed, double pullForceDecayRate, double minPullForceMultiplier, int decayDuration) {
        super(location, owner, id, pullForce, throwForce,
            pullForceDecayRate, minPullForceMultiplier, decayDuration);
        this.typeKey = typeKey;
        this.displayType = displayType;
        this.pullForce = pullForce;
        this.maxDistance = maxDistance;
        this.throwSpeed = throwSpeed;
        this.retractSpeed = retractSpeed;
    }

    public Key getTypeKey() {
        return typeKey;
    }

    public Class<? extends Display> getDisplayType() {
        return displayType;
    }

    public double getPullForce() {
        return pullForce;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public double getThrowSpeed() {
        return throwSpeed;
    }

    public double getRetractSpeed() {
        return retractSpeed;
    }

}
