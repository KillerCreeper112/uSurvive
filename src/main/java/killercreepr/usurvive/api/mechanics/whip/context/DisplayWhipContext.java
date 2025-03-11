package killercreepr.usurvive.api.mechanics.whip.context;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Display;

public interface DisplayWhipContext extends WhipContext {
    Key getTypeKey();
    Class<? extends Display> getDisplayType();
    int getMaxDistance();
    double getRetractSpeed();
}
