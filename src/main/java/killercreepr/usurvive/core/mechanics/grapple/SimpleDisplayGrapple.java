package killercreepr.usurvive.core.mechanics.grapple;

import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SimpleDisplayGrapple extends SimpleGrapple {
    protected final Key typeKey;
    protected final String displayType;
    protected final double pullForce;
    protected final int maxDistance;
    protected final double throwSpeed;
    protected final double retractSpeed;
    public SimpleDisplayGrapple(@NotNull Key hook,
                                int maxHooks,
                                int cooldown,
                                float throwForce, Key typeKey, String displayType, double pullForce, int maxDistance, double throwSpeed, double retractSpeed) {
        super(hook, maxHooks, cooldown, throwForce);
        this.typeKey = typeKey;
        this.displayType = displayType;
        this.pullForce = pullForce;
        this.maxDistance = maxDistance;
        this.throwSpeed = throwSpeed;
        this.retractSpeed = retractSpeed;
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

    @Override
    public GrappleContext buildGrappleContext(ItemUseContext ctx, int id) {
        Player p = ctx.getPlayer();
        return new DisplayGrappleContext(
            p.getEyeLocation(), p, id, throwForce,
            typeKey,
            "item".equalsIgnoreCase(displayType) ? ItemDisplay.class : BlockDisplay.class,
            pullForce,
            maxDistance,
            throwSpeed,
            retractSpeed
        );
    }

    public Key getTypeKey() {
        return typeKey;
    }

    public String getDisplayType() {
        return displayType;
    }
}
