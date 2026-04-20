package killercreepr.usurvive.api.enchant.component;

import org.bukkit.event.player.PlayerInteractEvent;

public interface CruxEnchantPhysicalInteractable {
  void onPlayerPhysicalInteract(PlayerInteractEvent event, int level);
}
