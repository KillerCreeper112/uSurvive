package killercreepr.usurvive.core.enchant.component;

import killercreepr.cruxblocks.api.event.CruxBlockBreakEvent;
import killercreepr.cruxblocks.core.mining.user.EntityMiner;
import killercreepr.usurvive.api.enchant.component.CruxEnchantBlockDrops;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class CollectorEnchant implements CruxEnchantBlockDrops {
  @Override
  public void onBlockDropItem(BlockDropItemEvent event, int level) {
    var drops = event.getItems();
    if(drops.isEmpty()) return;
    var p = event.getPlayer();
    drops.removeIf(drop ->{
      var item = drop.getItemStack();
      var cantAddList = p.getInventory().addItem(item.clone()).values();
      if(cantAddList.isEmpty()){
        drop.remove();
        return true;
      }
      int totalAmount = 0;
      for (ItemStack cannotAdd : cantAddList) {
        totalAmount +=  cannotAdd.getAmount();
      }
      item.setAmount(totalAmount);
      return false;
    });
  }

  @Override
  public void onCruxBlockBreak(CruxBlockBreakEvent event, int level, EntityMiner miner) {
    var drops = event.getDrops();
    if(drops == null || drops.isEmpty()) return;
    if(!(miner.getEntity() instanceof Player p)) return;
    drops.removeIf(item ->{
      var cantAddList = p.getInventory().addItem(item.clone()).values();
      if(cantAddList.isEmpty()){
        return true;
      }
      int totalAmount = 0;
      for (ItemStack cannotAdd : cantAddList) {
        totalAmount +=  cannotAdd.getAmount();
      }
      item.setAmount(totalAmount);
      return false;
    });
  }
}
