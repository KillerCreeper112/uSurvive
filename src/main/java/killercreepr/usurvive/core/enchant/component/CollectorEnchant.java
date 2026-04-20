package killercreepr.usurvive.core.enchant.component;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import killercreepr.cruxblocks.api.event.CruxBlockBreakEvent;
import killercreepr.cruxblocks.core.mining.user.EntityMiner;
import killercreepr.usurvive.api.enchant.component.CruxEnchantBlockDrops;
import net.kyori.adventure.util.TriState;
import org.bukkit.Registry;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class CollectorEnchant implements CruxEnchantBlockDrops {
  @Override
  public void onBlockDropItem(BlockDropItemEvent event, int level) {
    var drops = event.getItems();
    if(drops.isEmpty()) return;
    var p = event.getPlayer();
    var tool = p.getInventory().getItemInMainHand();
    var data = event.getBlockState().getBlockData();
    if(data.requiresCorrectToolForDrops() && !data.isPreferredTool(tool)) return;
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

  public boolean isValidTool(BlockData data, ItemStack item){
    if(!data.requiresCorrectToolForDrops()) return true;
    var type = data.getMaterial().asBlockType();
    if(item == null) return true;
    var tool = item.getDataOrDefault(DataComponentTypes.TOOL, item.getType().getDefaultData(DataComponentTypes.TOOL));
    if(tool == null) return true;
    for (Tool.Rule rule : tool.rules()) {
      if(rule.correctForDrops() != TriState.TRUE) continue;
      if(!rule.blocks().resolve(Registry.BLOCK).contains(type)) continue;
      return true;
    }
    return false;
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
