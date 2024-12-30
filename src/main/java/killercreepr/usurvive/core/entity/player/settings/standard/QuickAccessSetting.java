package killercreepr.usurvive.core.entity.player.settings.standard;

import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.core.entity.player.settings.SimplePlayerSetting;
import killercreepr.usurvive.core.item.USurviveItems;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuickAccessSetting extends SimplePlayerSetting {
    public QuickAccessSetting(@NotNull Key key, @NotNull PlayerSettingSelection defaultSelection, @NotNull List<PlayerSettingSelection> validSelections, @NotNull Map<PlayerSettingSelection, String> descriptions) {

        super(key, defaultSelection, validSelections, descriptions);
    }

    @Override
    public void onSettingChanged(@NotNull HumanEntity e, @NotNull PlayerSettingSelection selection) {
        super.onSettingChanged(e, selection);

        if(selection.equals(StandardPlayerSettingSelections.DISABLED)){
            findQuickAccessItems(e.getInventory()).forEach(item -> item.setAmount(0));
            return;
        }
        if(selection.equals(StandardPlayerSettingSelections.LOCKED)){
            Inventory inv = e.getInventory();
            int index = findQuickAccessItemSlot(inv);
            ItemStack quickAccess = USurviveItems.QUICK_ACCESS.buildItem();
            if(index == -1){
                ItemStack previous = inv.getItem(8);
                if(previous != null) previous = previous.clone();
                inv.setItem(8, quickAccess);
                if(previous != null){
                    inv.addItem(previous).values().forEach(drop ->{
                        e.getWorld().dropItem(e.getLocation(), drop, dropped -> dropped.setOwner(e.getUniqueId()));
                    });
                }
                return;
            }
            inv.setItem(index, quickAccess);
            return;
        }
        if(selection.equals(StandardPlayerSettingSelections.UNLOCKED)){
            Inventory inv = e.getInventory();
            int index = findQuickAccessItemSlot(inv);
            if(index == -1){
                ItemStack quickAccess = USurviveItems.QUICK_ACCESS.buildItem();
                inv.addItem(quickAccess);
            }
        }
    }

    public List<ItemStack> findQuickAccessItems(@NotNull Inventory inv){
        List<ItemStack> list = new ArrayList<>();
        for(ItemStack item : inv){
            if(PluginItem.isPluginItem(item, USurviveItems.QUICK_ACCESS)) list.add(item);
        }
        return list;
    }

    public ItemStack findQuickAccessItem(@NotNull Inventory inv){
        for(ItemStack item : inv){
            if(PluginItem.isPluginItem(item, USurviveItems.QUICK_ACCESS)) return item;
        }
        return null;
    }

    public int findQuickAccessItemSlot(@NotNull Inventory inv){
        int index = -1;
        for(ItemStack item : inv){
            index++;
            if(PluginItem.isPluginItem(item, USurviveItems.QUICK_ACCESS)) return index;
        }
        return -1;
    }
}
