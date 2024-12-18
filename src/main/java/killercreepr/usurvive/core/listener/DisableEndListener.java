package killercreepr.usurvive.core.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class DisableEndListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        if(b == null) return;
        if(b.getType() == Material.END_PORTAL_FRAME){
            event.setCancelled(true);
            p.sendMessage("The end is currently disabled for the time being.");
        }
    }

}
