package killercreepr.usurvive.core.mechanics.whip.context;

import killercreepr.usurvive.api.mechanics.whip.context.DisplayWhipContext;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DisplaySimpleWhipContext extends SimpleWhipContext implements DisplayWhipContext {
    protected final Key typeKey;
    protected final Class<? extends Display> displayType;
    protected final int maxDistance;
    protected final double retractSpeed;

    public DisplaySimpleWhipContext(@NotNull Location location, @NotNull Entity owner, int id, double throwForce, int pierce, Key typeKey, Class<? extends Display> displayType, int maxDistance, double retractSpeed) {
        super(location, owner, id, throwForce, pierce);
        this.typeKey = typeKey;
        this.displayType = displayType;
        this.maxDistance = maxDistance;
        this.retractSpeed = retractSpeed;
    }

    public Key getTypeKey() {
        return typeKey;
    }

    public Class<? extends Display> getDisplayType() {
        return displayType;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public double getRetractSpeed() {
        return retractSpeed;
    }
}
