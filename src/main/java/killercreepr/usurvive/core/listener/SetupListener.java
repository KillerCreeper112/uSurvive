package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.data.ResourcePackRequestData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
    public void onPlayerLogin(PlayerJoinEvent event) {
        ResourcePackRequestData data = cfg.RESOURCE_PACKS().value();
        if(data == null) return;
        Player p = event.getPlayer();
        p.sendResourcePacks(data.buildRequest());
    }

}
