package killercreepr.usurvive.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import killercreepr.usurvive.values.ValuesProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerPingListener implements Listener {
    protected final ValuesProvider cfg;

    public ServerPingListener(ValuesProvider cfg) {
        this.cfg = cfg;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPaperServerListPing(PaperServerListPingEvent event) {
        Component c = cfg.SERVER_MOTD().value();
        if(c == null) return;
        event.motd(c);
    }

}
