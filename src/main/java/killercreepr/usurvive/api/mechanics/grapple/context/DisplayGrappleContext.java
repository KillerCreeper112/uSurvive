package killercreepr.usurvive.api.mechanics.grapple.context;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Display;

public interface DisplayGrappleContext extends GrappleContext{
    Key getTypeKey();

    Class<? extends Display> getDisplayType();
    double getPullForce();

    int getMaxDistance();

    double getThrowSpeed();

    double getRetractSpeed();

    double getPullForceDecayRate();

    double getMinPullForceMultiplier();

    int getDecayDuration();
}
