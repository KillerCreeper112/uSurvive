package killercreepr.usurvive.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EntityTrampleEnchantEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final @NotNull LivingEntity user;
    protected final @NotNull Entity victim;
    protected double damage;
    protected boolean cancel = false;

    public EntityTrampleEnchantEvent(@NotNull LivingEntity user, @NotNull Entity victim, double damage) {
        this.user = user;
        this.victim = victim;
        this.damage = damage;
    }

    @NotNull
    public LivingEntity getUser() {
        return user;
    }

    @NotNull
    public Entity getVictim() {
        return victim;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
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
