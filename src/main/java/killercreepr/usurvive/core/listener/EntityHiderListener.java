package killercreepr.usurvive.core.listener;

import killercreepr.usurvive.api.entity.hider.EntityHider;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class EntityHiderListener implements Listener {
    protected final Plugin plugin;

    public EntityHiderListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        var p = event.getPlayer();
        forEachOnline(online ->{
            if(online.equals(p)) return;
            EntityHider hider = EntityHider.entityHider(online);
            if(hider == null) return;
            hider.updateVisibility(p);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        var p = event.getPlayer();
        forEachOnline(online ->{
            if(online.equals(p)) return;
            EntityHider hider = EntityHider.entityHider(online);
            if(hider == null) return;
            hider.show(p);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        var p = event.getPlayer();
        forEachOnline(online ->{
            if(online.equals(p)) return;
            EntityHider hider = EntityHider.entityHider(online);
            if(hider == null) return;
            hider.updateVisibility(p);
        });
    }

    public void forEachOnline(Consumer<Player> consumer){
        plugin.getServer().getOnlinePlayers().forEach(consumer);
    }
}
