package killercreepr.usurvive.external.discordsrv;

import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AchievementMessagePreProcessEvent;
import github.scarsz.discordsrv.api.events.DeathMessagePreProcessEvent;
import github.scarsz.discordsrv.api.events.Event;
import github.scarsz.discordsrv.api.events.GameChatMessagePreProcessEvent;
import killercreepr.usurvive.core.vanish.VanishHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class DiscordSRVListener {
  @Subscribe(priority = ListenerPriority.LOWEST)
  public void onGameChatMessagePreProcess(GameChatMessagePreProcessEvent event){
    var p = event.getPlayer();
    if(VanishHandler.isVanished(p)){
      event.setCancelled(true);
    }
  }

  @Subscribe(priority = ListenerPriority.LOWEST)
  public void onAchievementMessagePreProcess(AchievementMessagePreProcessEvent event){
    var p = event.getPlayer();
    if(VanishHandler.isVanished(p)){
      event.setCancelled(true);
    }
  }

  @Subscribe(priority = ListenerPriority.LOWEST)
  public void onDeathMessagePreProcess(DeathMessagePreProcessEvent event){
    var p = event.getPlayer();
    if(VanishHandler.isVanished(p)){
      event.setCancelled(true);
    }
  }
}
