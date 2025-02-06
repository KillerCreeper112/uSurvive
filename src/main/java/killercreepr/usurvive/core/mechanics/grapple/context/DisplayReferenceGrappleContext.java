package killercreepr.usurvive.core.mechanics.grapple.context;

import killercreepr.usurvive.api.mechanics.grapple.context.DisplayGrappleContext;
import killercreepr.usurvive.core.mechanics.grapple.SimpleDisplayGrapple;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

public class DisplayReferenceGrappleContext extends SimpleGrappleContext implements DisplayGrappleContext {
    protected final SimpleDisplayGrapple grapple;
    public DisplayReferenceGrappleContext(@NotNull Location location, @NotNull Entity owner, int id, double force, SimpleDisplayGrapple grapple) {
        super(location, owner, id, force);
        this.grapple = grapple;
    }

    @Override
    public Key getTypeKey() {
        return grapple.getTypeKey();
    }

    @Override
    public Class<? extends Display> getDisplayType() {
        return "item".equalsIgnoreCase(grapple.getDisplayType()) ? ItemDisplay.class : BlockDisplay.class;
    }

    @Override
    public double getPullForce() {
        return grapple.getPullForce();
    }

    @Override
    public int getMaxDistance() {
        return grapple.getMaxDistance();
    }

    @Override
    public double getThrowSpeed() {
        return grapple.getThrowSpeed();
    }

    @Override
    public double getRetractSpeed() {
        return grapple.getRetractSpeed();
    }

    @Override
    public double getPullForceDecayRate() {
        return grapple.getPullForceDecayRate();
    }

    @Override
    public double getMinPullForceMultiplier() {
        return grapple.getMinPullForceMultiplier();
    }

    @Override
    public int getDecayDuration() {
        return grapple.getDecayDuration();
    }
}
