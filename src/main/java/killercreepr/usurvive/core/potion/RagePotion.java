package killercreepr.usurvive.core.potion;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.SimpleActivePotion;
import killercreepr.usurvive.core.potion.rage.RagePotionHandler;
import killercreepr.usurvive.core.potion.rage.RagePotionMobHandler;
import killercreepr.usurvive.core.potion.rage.RagePotionPlayerHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.jetbrains.annotations.NotNull;

public class RagePotion extends SimpleActivePotion {
    protected final RagePotionHandler handler;
    public RagePotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(potion, entity, duration, amplifier);
        if(entity instanceof Player d) this.handler = new RagePotionPlayerHandler(d, this);
        else if(entity instanceof Mob ee) this.handler = new RagePotionMobHandler(ee, this);
        else{
            handler = null;
            this.duration = 0;
        }
    }

    @Override
    protected void onStop() {
        if(handler==null) return;
        handler.stopped();
    }

    @Override
    protected void onStart() {
        if(handler==null) return;
        handler.started();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        if(handler == null) return;
        handler.onEntityRemoveFromWorld(event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        if(handler == null) return;
        handler.onEntityDeath(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerStopSpectatingEntity(PlayerStopSpectatingEntityEvent event) {
        if(handler == null) return;
        handler.onPlayerStopSpectatingEntity(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerStartSpectatingEntity(PlayerStartSpectatingEntityEvent event) {
        if(handler == null) return;
        handler.onPlayerStartSpectatingEntity(event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if(handler == null) return;
        handler.onPlayerGameModeChange(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDismount(EntityDismountEvent event) {
        if(handler == null) return;
        handler.onEntityDismount(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if(handler == null) return;
        handler.onEntityTarget(event);
    }

    @Override
    protected void onTick() {
        if(handler != null) handler.tick();
    }

    public RagePotionHandler getHandler() {
        return handler;
    }
}
