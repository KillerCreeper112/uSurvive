package killercreepr.usurvive.core.enchant.component;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.util.Pair;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.paper.ItemHolder;
import killercreepr.cruxblocks.api.block.component.BushBlock;
import killercreepr.usurvive.api.enchant.component.CruxEnchantInteractable;
import killercreepr.usurvive.core.USurvivePlugin;
import net.kyori.adventure.key.Key;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Bisected;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutoPlantEnchant implements CruxEnchantInteractable {
  public final int maxLevel;

  public AutoPlantEnchant(int maxLevel) {
    this.maxLevel = maxLevel;
  }

  @Override
  public void onPlayerInteract(PlayerInteractEvent event, int level) {
    if(!event.getAction().isRightClick()) return;
    var p = event.getPlayer();
    var block = event.getClickedBlock();
    if(block == null || block.isEmpty()) return;

    var item = event.getItem();
    var result = harvestBlock(block, p, item, level);
    if(result){
      var hand = event.getHand();
      if(hand != null) p.swingHand(hand);
    }
  }

  public boolean harvestBlock(Block block, Player p, ItemStack item, int level){
    if(!(block.getBlockData() instanceof Ageable ageable)) return false;
    if(ageable.getAge() < ageable.getMaximumAge()) return false;
    if(ageable instanceof Bisected bisected){
      if(bisected.getHalf() == Bisected.Half.TOP){
        var bottom = block.getRelative(BlockFace.DOWN);
        if(bottom.getType() != block.getType()) return false;
        if(!(bottom.getBlockData() instanceof Bisected bottomBisected)) return false;
        if(bottomBisected.getHalf() != Bisected.Half.BOTTOM) return false;
        block = bottom;
      }
    }


    var type = block.getType();

    var seeds = seeds(type.key());
    if(seeds == null) return false;

    int cost = getRequiredCost(level);
    var result = findSeeds(p.getInventory(), seeds.key(), cost);
    if(!result.getSecond()) return false;

    var sounds = block.getBlockSoundGroup();
    if(!p.breakBlock(block)) return false;

    var removed = 0;
    for (ItemStack seed : result.getFirst()) {
      var toRemove = Math.min(seed.getAmount(), cost - removed);
      seed.setAmount(seed.getAmount() - toRemove);
      removed += toRemove;
      if(toRemove >= cost) break;
    }
    if(item != null) p.damageItemStack(item, cost);

    block.setType(type, false);

    var loc = block.getLocation().toCenterLocation();
    new ParticleBuilder(Particle.HAPPY_VILLAGER)
      .location(loc)
      .count(CruxMath.random(3, 5))
      .extra(0.1)
      .offset(0.3, 0.3, 0.3)
      .spawn();
    block.getWorld().playSound(loc, sounds.getBreakSound(), SoundCategory.BLOCKS, sounds.getVolume(), sounds.getPitch());
    return true;
  }

  public Pair<List<ItemStack>, Boolean> findSeeds(Inventory inventory, Key seedType, int required){
    var list = new ArrayList<ItemStack>();
    var got = 0;
    for (ItemStack item : inventory.getContents()) {
      if(CruxItem.isEmpty(item)) continue;
      var type = Crux.handlers().item().getType(item);
      if(seedType.equals(type)){
        got += item.getAmount();
        list.add(item);
        if(got >= required) break;
      }
    }
    return Pair.of(list, got >= required);
  }

  public int getRequiredCost(int level){
    return Math.max(1, maxLevel - (level-1));
  }

  public ItemHolder seeds(Key type){
    var cfg = USurvivePlugin.inst().cfgContainer().ENCHANTS;
    var item = cfg.BLOCK_TO_SEEDS.valueOr(Map.of()).get(type);
    if(item != null) return item;
    return Crux.handlers().item().getItem(Key.key(type.namespace(), type.value() + "_seeds"));
  }
}
