package killercreepr.usurvive.api.event;

import killercreepr.crux.core.Crux;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerPremiumPurchaseEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final Player player;
    protected final double spent;
    protected final String reason;

    public PlayerPremiumPurchaseEvent(Player player, double spent, String reason) {
        super(!Crux.isPrimaryThread());
        this.player = player;
        this.spent = spent;
        this.reason = reason;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
