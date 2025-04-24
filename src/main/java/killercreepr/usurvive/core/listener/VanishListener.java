package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendSuggestionsEvent;
import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import killercreepr.crux.core.Crux;
import killercreepr.usurvive.core.vanish.VanishHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class VanishListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPaperServerListPing(PaperServerListPingEvent event) {
        AtomicInteger removed = new AtomicInteger(0);
        event.getListedPlayers().removeIf(info ->{
            Player p = Crux.getServer().getPlayer(info.id());
            if(p == null){
                removed.addAndGet(1);
                return true;
            }
            if(!VanishHandler.isVanished(p)) return false;
            removed.addAndGet(1);
            return true;
        });
        if(removed.get() == 0) return;
        event.setNumPlayers(Math.max(event.getNumPlayers() - removed.get(), 0));
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        if(!VanishHandler.isVanished(event.getPlayer())) return;
        event.message(null);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerSendSuggestions(AsyncPlayerSendSuggestionsEvent event) {
        event.getSuggestions().getList().removeIf(s ->{
            Player p = Crux.getServer().getPlayer(s.getText());
            if(p == null) return false;
            return VanishHandler.isVanished(p);
        });
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        VanishHandler.onJoin(p);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        VanishHandler.onJoin(p);
        if(!VanishHandler.isVanished(p)) return;
        event.joinMessage(null);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if(!VanishHandler.isVanished(p)) return;
        event.quitMessage(null);
    }

}
