package killercreepr.usurvive.api.enchant.component;

import org.bukkit.event.player.PlayerInteractEvent;

public interface CruxEnchantInteractable {
  void onPlayerInteract(PlayerInteractEvent event, int level);
}
