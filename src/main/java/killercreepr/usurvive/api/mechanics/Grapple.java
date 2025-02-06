package killercreepr.usurvive.api.mechanics;

import killercreepr.cruxitems.api.item.component.InteractableComponent;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public interface Grapple extends InteractableComponent {
    int getMaxHooks();
    int getCooldown();
    float getPullForce();
    float getThrowForce();
    float getPullForceDecayRate();
    float getMinPullForceMultiplier();
    int getDecayDuration();
    @NotNull Key getHookType();
}
