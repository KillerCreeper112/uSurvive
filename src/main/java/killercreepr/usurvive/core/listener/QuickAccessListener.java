package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.core.data.entity.MemoryPlayerSettingsHolder;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettingSelections;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettings;
import killercreepr.usurvive.core.item.USurviveItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class QuickAccessListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(e ->{
            if(PluginItem.isPluginItem(e, USurviveItems.QUICK_ACCESS)){
                event.getItemsToKeep().add(e);
                return true;
            }
            return false;
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if(PluginItem.isPluginItem(event.getMainHandItem(), USurviveItems.QUICK_ACCESS)) event.setCancelled(true);
        else if(PluginItem.isPluginItem(event.getOffHandItem(), USurviveItems.QUICK_ACCESS)) event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if(!PluginItem.isPluginItem(item, USurviveItems.QUICK_ACCESS)) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        PlayerSettingsHolder settings = EntityMemory.getOrCreateDataHolder(p, MemoryPlayerSettingsHolder.class);
        if(settings != null){
            if(!settings.hasSetting(StandardPlayerSettings.QUICK_ACCESS_ITEM, StandardPlayerSettingSelections.LOCKED)) return;
        }
        Inventory inv = p.getInventory();
        int index = findQuickAccessItem(inv);
        ItemStack quickAccess = USurviveItems.QUICK_ACCESS.buildItem();
        if(index == -1){
            ItemStack previous = inv.getItem(8);
            if(previous != null) previous = previous.clone();
            inv.setItem(8, quickAccess);
            if(previous != null){
                inv.addItem(previous).values().forEach(drop ->{
                    p.getWorld().dropItem(p.getLocation(), drop, e -> e.setOwner(p.getUniqueId()));
                });
            }
            return;
        }
        inv.setItem(index, quickAccess);
    }

    public int findQuickAccessItem(@NotNull Inventory inv){
        int index = -1;
        for(ItemStack item : inv){
            index++;
            if(PluginItem.isPluginItem(item, USurviveItems.QUICK_ACCESS)) return index;
        }
        return -1;
    }
}
