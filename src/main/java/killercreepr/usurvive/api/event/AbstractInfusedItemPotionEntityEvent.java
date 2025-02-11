package killercreepr.usurvive.api.event;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AbstractInfusedItemPotionEntityEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final Entity entity;
    protected final CruxItem item;
    protected final CruxSlot slot;

    public AbstractInfusedItemPotionEntityEvent(Entity entity, CruxItem item, CruxSlot slot) {
        this.entity = entity;
        this.item = item;
        this.slot = slot;
    }

    public Entity getEntity() {
        return entity;
    }

    public CruxItem getItem() {
        return item;
    }

    public CruxSlot getSlot() {
        return slot;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }
}
