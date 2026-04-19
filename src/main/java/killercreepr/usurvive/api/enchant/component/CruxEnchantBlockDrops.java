package killercreepr.usurvive.api.enchant.component;

import killercreepr.cruxblocks.api.event.CruxBlockBreakEvent;
import killercreepr.cruxblocks.core.mining.user.EntityMiner;
import org.bukkit.event.block.BlockDropItemEvent;

public interface CruxEnchantBlockDrops {
  void onBlockDropItem(BlockDropItemEvent event, int level);
  void onCruxBlockBreak(CruxBlockBreakEvent event, int level, EntityMiner miner);
}
