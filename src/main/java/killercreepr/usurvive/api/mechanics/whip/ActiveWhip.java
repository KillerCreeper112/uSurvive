package killercreepr.usurvive.api.mechanics.whip;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ActiveWhip {
    int getId();
    boolean isValid(@NotNull Entity owner);
    default void remove(@Nullable Entity owner){
        scheduleForRemoval();
    }
    void tick(@NotNull Entity owner);
    boolean isScheduledForRemoval();
    void scheduleForRemoval();
}
