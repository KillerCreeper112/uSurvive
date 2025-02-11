package killercreepr.usurvive.api.event;

import killercreepr.crux.api.item.CruxItem;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PostItemInfusePotionsEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final ItemStack item;
    protected final List<PotionEffect> potions;
    protected final CruxItem result;
    protected boolean cancel = false;

    public PostItemInfusePotionsEvent(ItemStack item, List<PotionEffect> potions, CruxItem result) {
        this.item = item;
        this.potions = potions;
        this.result = result;
    }

    public CruxItem getResult() {
        return result;
    }

    public ItemStack getItem() {
        return item;
    }

    /**
     * @return Immutable list. The change has already happened. Use PreItemInfusePotionsEvent to edit potions.
     */
    public List<PotionEffect> getPotions() {
        return potions;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
