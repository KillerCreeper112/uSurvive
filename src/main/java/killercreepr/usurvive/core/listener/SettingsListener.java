package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.cruxmenus.api.event.MenuCloseEvent;
import killercreepr.usurvive.core.data.entity.SimplePlayerSettingsHolder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SettingsListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMenuClose(MenuCloseEvent event) {
        HumanEntity p = event.getPlayer();
        SimplePlayerSettingsHolder holder = EntityMemory.getOrCreateDataHolder(p, SimplePlayerSettingsHolder.class);
        if(holder == null) return;
        holder.setCache(null);
    }

}
