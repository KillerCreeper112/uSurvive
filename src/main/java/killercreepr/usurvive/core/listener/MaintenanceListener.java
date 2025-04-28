package killercreepr.usurvive.core.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class MaintenanceListener implements Listener {
    public static boolean maintenance = false;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(!maintenance) return;
        if(event.getUniqueId().equals(UUID.fromString("0b0600cf-f8c3-4fb3-9b9a-ab77d2432809"))) return;
        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        event.kickMessage(Component.text("MAINTENANCE"));
    }

}
