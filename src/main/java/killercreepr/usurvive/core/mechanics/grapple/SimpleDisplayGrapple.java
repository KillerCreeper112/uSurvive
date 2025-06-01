package killercreepr.usurvive.core.mechanics.grapple;

import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.usurvive.api.mechanics.grapple.context.GrappleContext;
import killercreepr.usurvive.core.mechanics.grapple.context.DisplayReferenceGrappleContext;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SimpleDisplayGrapple extends SimpleGrapple {
    protected final Key typeKey;
    protected final String displayType;
    protected final int maxDistance;
    protected final float retractSpeed;

    public SimpleDisplayGrapple(@NotNull Key hook, int maxHooks, int cooldown, float pullForce,
                                float throwForce, float pullForceDecayRate, float minPullForceMultiplier,
                                int decayDuration, float swingBoostSpeed, float ropeBoostSpeed, int itemCountUse,
                                float swapHandsClimbSpeed, Key typeKey, String displayType,
                                int maxDistance, float retractSpeed) {
        super(hook, maxHooks, cooldown, pullForce, throwForce, pullForceDecayRate, minPullForceMultiplier, decayDuration,
            swingBoostSpeed, ropeBoostSpeed, itemCountUse, swapHandsClimbSpeed);
        this.typeKey = typeKey;
        this.displayType = displayType;
        this.maxDistance = maxDistance;
        this.retractSpeed = retractSpeed;
    }

    /*public SimpleDisplayGrapple(@NotNull Key hook,
                                int maxHooks,
                                int cooldown,
                                float throwForce, Key typeKey, String displayType, float pullForce, int maxDistance,
                                float throwSpeed, float retractSpeed, float pullForceDecayRate, float minPullForceMultiplier, int decayDuration) {
        super(hook, maxHooks, cooldown, throwForce,
            pullForceDecayRate, minPullForceMultiplier, decayDuration);
        this.typeKey = typeKey;
        this.displayType = displayType;
        this.pullForce = pullForce;
        this.maxDistance = maxDistance;
        this.throwSpeed = throwSpeed;
        this.retractSpeed = retractSpeed;
    }*/

    public int getMaxDistance() {
        return maxDistance;
    }

    public float getRetractSpeed() {
        return retractSpeed;
    }

    @Override
    public GrappleContext buildGrappleContext(ItemUseContext ctx, int id) {
        Player p = ctx.getPlayer();
        return new DisplayReferenceGrappleContext(
            p.getEyeLocation(), p, id, this, swapHandsClimbSpeed
        );
    }

    public Key getTypeKey() {
        return typeKey;
    }

    public String getDisplayType() {
        return displayType;
    }
}
