package killercreepr.usurvive.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import killercreepr.usurvive.data.ResourcePackRequestData;
import killercreepr.usurvive.values.ValuesProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class SetupListener implements Listener {
    protected final ValuesProvider cfg;

    public SetupListener(ValuesProvider cfg) {
        this.cfg = cfg;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPaperServerListPing(PaperServerListPingEvent event) {
        Component c = cfg.SERVER_MOTD().value();
        if(c == null) return;
        event.motd(c);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        ResourcePackRequestData data = cfg.RESOURCE_PACKS().value();
        if(data == null) return;
        Player p = event.getPlayer();
        p.sendResourcePacks(data.buildRequest());
    }
}
