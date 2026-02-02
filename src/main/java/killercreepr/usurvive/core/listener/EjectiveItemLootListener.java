package killercreepr.usurvive.core.listener;

import killercreepr.usurvive.core.mechanics.EjectiveItemLootComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class EjectiveItemLootListener implements Listener {
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event){
    var p = event.getPlayer();
    var removed = EjectiveItemLootComponent.CURRENT_TASKS.remove(p.getUniqueId());
    if(removed == null) return;
    removed.forEach(EjectiveItemLootComponent.Task::cancel);
  }
}
