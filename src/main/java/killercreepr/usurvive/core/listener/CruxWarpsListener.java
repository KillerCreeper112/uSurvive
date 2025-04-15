package killercreepr.usurvive.core.listener;

import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxwarps.api.event.PlayerSetWarpEvent;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.world.module.ResetWorldModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CruxWarpsListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerSetWarp(PlayerSetWarpEvent event) {
        var warp = event.getWarp();
        CruxWorld crux = CruxCore.core().worldManager().getWorld(warp.world().worldKey());
        if(crux == null) return;
        if(crux.getModule(ResetWorldModule.class) == null) return;
        event.setCancelled(true);
        Lang.WARP_CANNOT_CREATE_IN_RESOURCE_WORLD.use(event.getEntity());
    }

}
