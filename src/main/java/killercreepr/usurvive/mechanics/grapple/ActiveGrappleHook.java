package killercreepr.usurvive.mechanics.grapple;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface ActiveGrappleHook {
    int getId();
    boolean isValid(@NotNull Entity owner);
    void remove();
    void tick(@NotNull Entity owner);
}
