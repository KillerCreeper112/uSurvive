package killercreepr.usurvive.api.event;

import killercreepr.usurvive.api.death.DeathManager;
import killercreepr.usurvive.core.death.PlayerDeath;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDeathExpireEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final @NotNull DeathManager deathManager;
    protected @NotNull PlayerDeath death;
    protected boolean cancel = false;
    public PlayerDeathExpireEvent(@NotNull DeathManager deathManager, @NotNull PlayerDeath death) {
        this.deathManager = deathManager;
        this.death = death;
    }

    public PlayerDeathExpireEvent(boolean isAsync, @NotNull DeathManager deathManager, @NotNull PlayerDeath death) {
        super(isAsync);
        this.deathManager = deathManager;
        this.death = death;
    }

    public boolean wouldSpawnNormally(){
        return !death.getDrops().isEmpty() || death.getExperience() > 0;
    }

    @NotNull
    public DeathManager getDeathManager() {
        return deathManager;
    }

    @NotNull
    public PlayerDeath getDeath() {
        return death;
    }

    public void setDeath(@NotNull PlayerDeath death) {
        this.death = death;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
