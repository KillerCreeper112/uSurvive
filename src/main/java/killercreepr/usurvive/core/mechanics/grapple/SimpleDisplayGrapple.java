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
    protected final double pullForce;
    protected final int maxDistance;
    protected final double throwSpeed;
    protected final double retractSpeed;
    protected final double pullForceDecayRate;
    protected final double minPullForceMultiplier;
    protected final int decayDuration;
    public SimpleDisplayGrapple(@NotNull Key hook,
                                int maxHooks,
                                int cooldown,
                                float throwForce, Key typeKey, String displayType, double pullForce, int maxDistance, double throwSpeed, double retractSpeed, double pullForceDecayRate, double minPullForceMultiplier, int decayDuration) {
        super(hook, maxHooks, cooldown, throwForce);
        this.typeKey = typeKey;
        this.displayType = displayType;
        this.pullForce = pullForce;
        this.maxDistance = maxDistance;
        this.throwSpeed = throwSpeed;
        this.retractSpeed = retractSpeed;
        this.pullForceDecayRate = pullForceDecayRate;
        this.minPullForceMultiplier = minPullForceMultiplier;
        this.decayDuration = decayDuration;
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

    public double getPullForceDecayRate() {
        return pullForceDecayRate;
    }

    public double getMinPullForceMultiplier() {
        return minPullForceMultiplier;
    }

    public int getDecayDuration() {
        return decayDuration;
    }

    @Override
    public GrappleContext buildGrappleContext(ItemUseContext ctx, int id) {
        Player p = ctx.getPlayer();
        return new DisplayReferenceGrappleContext(
            p.getEyeLocation(), p, id, throwForce, this
        );
    }

    public Key getTypeKey() {
        return typeKey;
    }

    public String getDisplayType() {
        return displayType;
    }
}
