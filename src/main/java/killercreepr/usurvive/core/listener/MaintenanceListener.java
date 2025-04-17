package killercreepr.usurvive.core.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class MaintenanceListener implements Listener {
    public static boolean maintenance = false;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(!maintenance) return;
        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        event.kickMessage(Component.text("MAINTENANCE"));
    }

}
