package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxcore.CruxCore;
import killercreepr.usurvive.api.component.WaterProjectile;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.entity.memory.WaterProjectileHolder;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MiscListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityPlace(EntityPlaceEvent event) {
        if(event.getEntity() instanceof ArmorStand s){
            s.setArms(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        ItemStack arrow = event.getConsumable();
        if(CruxItem.isEmpty(arrow)) return;
        CruxItem crux = CruxItem.wrap(arrow);
        WaterProjectile water = crux.getOrDefaultData(USurviveComponents.WATER_PROJECTILE);
        if(water==null) return;
        EntityMemory.getOrCreateDataHolder(event.getProjectile(), WaterProjectileHolder.class, mem ->{
            event.getProjectile().setPersistent(false);
            return new WaterProjectileHolder(mem, water);
        });
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockDispenseLoot(BlockDispenseLootEvent event) {
        var loot = event.getDispensedLoot();
        Random random;
        World world = event.getBlock().getWorld();
        var crux = CruxCore.core().worldManager().getWorld(world.key());
        if(crux == null) random = new Random(world.getSeed());
        else random = crux.getRandom();
        USurvivePlugin.inst().values().BLOCK_DISPENSE_LOOT_DATA().ifPresent(list ->{
            list.forEach(data ->{
                if(!CruxMath.testChance(random, data.chance())) return;
                ItemStack item = data.item().value().clone();
                int amount = data.amount().sample(random).intValue();
                int maxStack = CruxItem.getMaxStackSize(item);
                while(amount > 0){
                    int x = Math.min(maxStack, amount);
                    amount -= x;
                    ItemStack clone = item.clone();
                    clone.setAmount(x);
                    loot.add(clone);
                }
            });
        });
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onLootGenerate(LootGenerateEvent event) {
        var lootTable = event.getLootTable();
        if(!lootTable.key().value().startsWith("chests/")) return;
        var loot = event.getLoot();
        Random random;
        World world = event.getWorld();
        var crux = CruxCore.core().worldManager().getWorld(world.key());
        if(crux == null) random = new Random(world.getSeed());
        else random = crux.getRandom();
        USurvivePlugin.inst().values().CHESTS_LOOT_DATA().ifPresent(list ->{
            list.forEach(data ->{
                if(!CruxMath.testChance(random, data.chance())) return;
                ItemStack item = data.item().value().clone();
                int amount = data.amount().sample(random).intValue();
                int maxStack = CruxItem.getMaxStackSize(item);
                while(amount > 0){
                    int x = Math.min(maxStack, amount);
                    amount -= x;
                    ItemStack clone = item.clone();
                    clone.setAmount(x);
                    loot.add(clone);
                }
            });
        });
    }

}
