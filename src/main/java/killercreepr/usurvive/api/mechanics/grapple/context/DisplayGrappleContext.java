package killercreepr.usurvive.api.mechanics.grapple.context;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Display;

public interface DisplayGrappleContext extends GrappleContext{
    Key getTypeKey();
    Class<? extends Display> getDisplayType();
    int getMaxDistance();
    double getRetractSpeed();
}
