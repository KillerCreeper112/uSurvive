package killercreepr.usurvive.api.event;

import killercreepr.usurvive.api.entity.sleeper.SlumberData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EntityDeepSlumberEvent extends Event /*implements Cancellable*/ {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final Entity entity;
    protected final SlumberData slumber;
    protected boolean cancel = false;

    public EntityDeepSlumberEvent(Entity entity, SlumberData slumber) {
        super(!Bukkit.isPrimaryThread());
        this.entity = entity;
        this.slumber = slumber;
    }

    public SlumberData getSlumber() {
        return slumber;
    }

    public Entity getEntity() {
        return entity;
    }

    /*@Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }*/

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
