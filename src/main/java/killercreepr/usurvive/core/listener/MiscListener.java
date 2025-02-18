package killercreepr.usurvive.core.listener;

import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;

public class MiscListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityPlace(EntityPlaceEvent event) {
        if(event.getEntity() instanceof ArmorStand s){
            s.setArms(true);
        }
    }

}
