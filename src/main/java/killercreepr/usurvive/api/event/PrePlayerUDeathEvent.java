package killercreepr.usurvive.api.event;

import killercreepr.usurvive.api.death.DeathManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class PrePlayerUDeathEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final Player player;
    protected final @NotNull DeathManager deathManager;
    protected boolean cancel = false;
    protected final PlayerDeathEvent playerDeathEvent;

    public PrePlayerUDeathEvent(Player player, @NotNull DeathManager deathManager, PlayerDeathEvent playerDeathEvent) {
        this.player = player;
        this.deathManager = deathManager;
        this.playerDeathEvent = playerDeathEvent;
    }

    public PlayerDeathEvent getPlayerDeathEvent() {
        return playerDeathEvent;
    }

    @NotNull
    public DeathManager getDeathManager() {
        return deathManager;
    }

    public Player getPlayer() {
        return player;
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
