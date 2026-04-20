package killercreepr.usurvive.core.enchant.component;

import killercreepr.usurvive.api.enchant.component.CruxEnchantPhysicalInteractable;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SoftStepEnchant implements CruxEnchantPhysicalInteractable {
  public final int maxLevel;

  public SoftStepEnchant(int maxLevel) {
    this.maxLevel = maxLevel;
  }

  @Override
  public void onPlayerPhysicalInteract(PlayerInteractEvent event, int level) {
    var action = event.getAction();
    if(action != Action.PHYSICAL) return;
    var block = event.getClickedBlock();
    if(block == null) return;
    if(block.getType() != Material.FARMLAND) return;
    event.setCancelled(true);
  }
}
