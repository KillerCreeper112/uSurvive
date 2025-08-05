package killercreepr.usurvive.api.event;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ProjectileRicochetEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final Entity projectile;
    protected Entity newProjectile;
    protected Vector reflectDirection;
    protected int reflectedAmount;
    protected final Block hitBlock;
    protected final BlockFace hitBlockFace;
    protected final Entity hitEntity;
    protected boolean cancel = false;

    public ProjectileRicochetEvent(Entity projectile, Entity newProjectile, Vector reflectDirection, int reflectedAmount, Block hitBlock, BlockFace hitBlockFace, Entity hitEntity) {
        this.projectile = projectile;
        this.newProjectile = newProjectile;
        this.reflectDirection = reflectDirection;
        this.reflectedAmount = reflectedAmount;
        this.hitBlock = hitBlock;
        this.hitBlockFace = hitBlockFace;
        this.hitEntity = hitEntity;
    }

    public Entity getProjectile() {
        return projectile;
    }

    public Entity getNewProjectile() {
        return newProjectile;
    }

    public Vector getReflectDirection() {
        return reflectDirection;
    }

    public int getReflectedAmount() {
        return reflectedAmount;
    }

    public Block getHitBlock() {
        return hitBlock;
    }

    public BlockFace getHitBlockFace() {
        return hitBlockFace;
    }

    public Entity getHitEntity() {
        return hitEntity;
    }

    public void setReflectedAmount(int reflectedAmount) {
        this.reflectedAmount = reflectedAmount;
    }

    public void setReflectDirection(Vector reflectDirection) {
        this.reflectDirection = reflectDirection;
    }

    public void setNewProjectile(Entity newProjectile) {
        this.newProjectile = newProjectile;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
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
