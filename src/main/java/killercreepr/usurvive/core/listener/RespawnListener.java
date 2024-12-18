package killercreepr.usurvive.core.listener;

import killercreepr.usurvive.api.values.ValuesProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

public class RespawnListener implements Listener {
    protected final Plugin plugin;
    protected final ValuesProvider cfg;

    public RespawnListener(Plugin plugin, ValuesProvider cfg) {
        this.plugin = plugin;
        this.cfg = cfg;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        Location respawn = p.getRespawnLocation();
        if(respawn != null){
            event.setRespawnLocation(respawn);
            return;
        }
        Location spawn = cfg.RESPAWN_LOCATION().value();
        if(spawn==null) return;
        event.setRespawnLocation(spawn);
    }

}
