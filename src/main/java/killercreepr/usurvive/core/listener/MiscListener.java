package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.usurvive.api.component.WaterProjectile;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.entity.memory.WaterProjectileHolder;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

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
        EntityMemory.getOrCreateDataHolder(event.getProjectile(), WaterProjectileHolder.class, mem -> new WaterProjectileHolder(mem, water));
    }

}
