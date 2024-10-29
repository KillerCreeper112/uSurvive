package killercreepr.usurvive.listener;

import killercreepr.usurvive.values.ValuesProvider;
import org.bukkit.Location;
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Location spawn = cfg.RESPAWN_LOCATION().value();
        if(spawn==null) return;
        event.setRespawnLocation(spawn);
    }

}
