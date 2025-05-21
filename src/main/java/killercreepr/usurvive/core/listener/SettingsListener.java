package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxmenus.api.event.MenuCloseEvent;
import killercreepr.cruxmenus.api.menu.holder.MenuHolder;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsManager;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.data.entity.MemoryPlayerSettingsHolder;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettingSelections;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettings;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;

public class SettingsListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMenuClose(MenuCloseEvent event) {
        var p = event.getEntity();
        MemoryPlayerSettingsHolder holder = EntityMemory.getDataHolder(p, MemoryPlayerSettingsHolder.class);
        if(holder == null) return;
        holder.setCache(null);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity p = event.getWhoClicked();
        if(event.getClick() != ClickType.SWAP_OFFHAND) return;
        if(!(event.getView().getTopInventory() instanceof CraftingInventory)) return;
        PlayerSettingsManager manager = USurvivePlugin.inst().getPlayerSettingsManager();
        var holder = manager.getPlayerSettingsHolder(p);
        if(holder == null) return;
        if(!holder.hasSetting(StandardPlayerSettings.QUICK_RECIPE_BOOK, StandardPlayerSettingSelections.ENABLED)) return;

        MenuHolder menuHolder = CruxCore.core().cruxMenus().menuRegistry().menuHolders().get(Crux.key("crafting/standard/recipe_list"));
        if(menuHolder == null) return;
        event.setCancelled(true);
        menuHolder.open(p);
    }

}
