package killercreepr.usurvive.api.event;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.potion.PotionEffect;

public class InfusedItemPotionAddToEntityEvent extends AbstractInfusedItemPotionEntityEvent implements Cancellable {
    protected boolean cancel = false;
    protected final PotionEffect effect;
    public InfusedItemPotionAddToEntityEvent(Entity entity, CruxItem item, CruxSlot slot, PotionEffect effect) {
        super(entity, item, slot);
        this.effect = effect;
    }

    public PotionEffect getEffect() {
        return effect;
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
