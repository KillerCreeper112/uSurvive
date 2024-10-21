package killercreepr.usurvive.listener;

import killercreepr.crux.data.communication.Communicator;
import killercreepr.usurvive.values.ValuesProvider;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class SpawnListener implements Listener {
    protected final ValuesProvider cfg;
    protected final Plugin plugin;

    public SpawnListener(ValuesProvider cfg, Plugin plugin) {
        this.cfg = cfg;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if(p.hasPlayedBefore()) return;
        String worldName = cfg.SPAWN_WORLD().value();
        if(worldName == null) return;
        World world = plugin.getServer().getWorld(worldName);
        if(world == null){
            Communicator.chat("<red>An error has occurred - " + worldName).use(p);
            return;
        }
        p.teleport(world.getSpawnLocation());
    }

}
