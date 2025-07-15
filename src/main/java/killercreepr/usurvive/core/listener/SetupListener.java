package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.data.ResourcePackRequestData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.resource.ResourcePackCallback;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerConnectionClose(PlayerConnectionCloseEvent event) {
        var latch = connectionConfigure.remove(event.getPlayerUniqueId());
        if(latch == null) return;
        latch.countDown();
    }

    protected final Map<UUID, CountDownLatch> connectionConfigure = new HashMap<>();
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerConnectionConfigure(AsyncPlayerConnectionConfigureEvent event) throws InterruptedException {
        var latch = new CountDownLatch(1);
        ResourcePackRequestData data = cfg.RESOURCE_PACKS().value();
        if(data == null) return;
        UUID uuid = event.getConnection().getProfile().getId();
        if(uuid == null){
            event.getConnection().disconnect(Component.text("UUID not found."));
            return;
        }
        var connection = event.getConnection();
        Audience aud = connection.getAudience();
        aud.sendResourcePacks(
            data.buildRequest()
                .callback(
                    ResourcePackCallback.onTerminal(
                        (packId, audience) -> {
                            // loaded successfully
                            latch.countDown();
                            connectionConfigure.remove(uuid);
                        },
                        (packId, audience) -> {
                            // rejected or could not apply
                            latch.countDown();
                            connectionConfigure.remove(uuid);
                            connection.disconnect(Component.text("Server requires custom resource pack"));
                        }
                    )
                )
        );
        connectionConfigure.put(uuid, latch);
        latch.await(2L, TimeUnit.MINUTES);
        if(latch.getCount() > 0){
            connectionConfigure.remove(uuid);
            latch.countDown();
            connection.disconnect(Component.text("Server requires custom resource pack"));
        }
    }
    /*@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerJoinEvent event) {
        ResourcePackRequestData data = cfg.RESOURCE_PACKS().value();
        if(data == null) return;
        Player p = event.getPlayer();
        p.sendResourcePacks(data.buildRequest());
    }*/

}
