package killercreepr.usurvive.api.event;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.usurvive.api.component.InfusedPotions;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.potion.PotionEffect;

public class InfusedItemUpdateEvent extends AbstractInfusedItemPotionEntityEvent implements Cancellable {
    protected boolean cancel = false;
    protected final InfusedPotions currentInfusedPotions;
    protected InfusedPotions newInfusedPotions;

    public InfusedItemUpdateEvent(Entity entity, CruxItem item, CruxSlot slot, InfusedPotions currentInfusedPotions, InfusedPotions newInfusedPotions) {
        super(entity, item, slot);
        this.currentInfusedPotions = currentInfusedPotions;
        this.newInfusedPotions = newInfusedPotions;
    }

    public void setNewInfusedPotions(InfusedPotions newInfusedPotions) {
        this.newInfusedPotions = newInfusedPotions;
    }

    public InfusedPotions getCurrentInfusedPotions() {
        return currentInfusedPotions;
    }

    public InfusedPotions getNewInfusedPotions() {
        return newInfusedPotions;
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
