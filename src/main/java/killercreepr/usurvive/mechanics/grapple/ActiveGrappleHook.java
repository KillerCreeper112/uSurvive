package killercreepr.usurvive.mechanics.grapple;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ActiveGrappleHook {
    int getId();
    boolean isValid(@NotNull Entity owner);
    default void remove(@Nullable Entity owner){
        scheduleForRemoval();
    }
    void tick(@NotNull Entity owner);
    boolean isScheduledForRemoval();
    void scheduleForRemoval();
}
