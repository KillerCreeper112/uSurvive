package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.usurvive.api.entity.sleeper.SlumberData;
import killercreepr.usurvive.api.entity.sleeper.SlumberSpectator;
import killercreepr.usurvive.core.entity.memory.SleeperHolder;
import killercreepr.usurvive.core.entity.sleeper.BedSlumberData;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class BedListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        if(b == null) return;
        if(!(b.getBlockData() instanceof Bed)) return;
        event.setCancelled(true);

        SleeperHolder sleeper = EntityMemory.getDataHolder(p, SleeperHolder.class);
        if(sleeper==null) return;
        SlumberData slumberData = new BedSlumberData(p, b.getLocation());
        sleeper.sleep(slumberData);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerStopSpectatingEntity(PlayerStopSpectatingEntityEvent event) {
        Player p = event.getPlayer();
        SleeperHolder sleeper = EntityMemory.getDataHolder(p, SleeperHolder.class);
        if(sleeper==null) return;
        if(!sleeper.isSleeping()) return;
        if(!(sleeper.getSlumberData() instanceof SlumberSpectator spec)) return;
        spec.onStopSpectating(event);
    }
}
