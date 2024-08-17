package killercreepr.usurvive.listener;

import killercreepr.crux.data.entity.EntityMemory;
import killercreepr.usurvive.data.entity.GrapplerHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class GrappleListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player p = event.getPlayer();
        GrapplerHolder grappler = EntityMemory.getDataHolder(p, GrapplerHolder.class);
        if(grappler==null) return;
        grappler.reset();
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityUnleash(EntityUnleashEvent event) {

    }

}
