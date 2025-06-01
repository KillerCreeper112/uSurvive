package killercreepr.usurvive.api.mechanics.grapple;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ActiveGrappleHook {
    int getId();
    boolean isValid(@NotNull Entity owner);
    default void remove(@Nullable Entity owner){
        scheduleForRemoval();
    }
    default void onRemoving(@Nullable Entity owner){
    }

    default boolean hasSwapHandAction(){
        return false;
    }

    default void onSwapHands(PlayerSwapHandItemsEvent event){

    }

    /**
     * Called when the owner manually removes the hook
     */
    default void forceRemove(@NotNull Entity owner){
        remove(owner);
    }

    void tick(@NotNull Entity owner, @NotNull Collection<ActiveGrappleHook> activeHooks);
    boolean isScheduledForRemoval();
    void scheduleForRemoval();

    Location getHookLocation();
    boolean canUseAsAnchor();
}
