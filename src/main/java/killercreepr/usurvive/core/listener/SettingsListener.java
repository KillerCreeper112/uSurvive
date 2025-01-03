package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.cruxmenus.api.event.MenuCloseEvent;
import killercreepr.usurvive.core.data.entity.MemoryPlayerSettingsHolder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SettingsListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMenuClose(MenuCloseEvent event) {
        HumanEntity p = event.getPlayer();
        MemoryPlayerSettingsHolder holder = EntityMemory.getOrCreateDataHolder(p, MemoryPlayerSettingsHolder.class);
        if(holder == null) return;
        holder.setCache(null);
    }

}
