package killercreepr.usurvive.api.mechanics.whip;

import killercreepr.crux.api.data.CruxKeyed;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ActiveWhip extends CruxKeyed {
    boolean isValid(@NotNull Entity owner);
    default void remove(@Nullable Entity owner){
        scheduleForRemoval();
    }
    void tick(@NotNull Entity owner);
    boolean isScheduledForRemoval();
    void scheduleForRemoval();
}
