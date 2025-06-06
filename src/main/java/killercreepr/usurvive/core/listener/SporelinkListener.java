package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.cruxpotions.core.entity.memory.SimplePotionHolder;
import killercreepr.usurvive.core.potion.SporelinkPotion;
import killercreepr.usurvive.core.potion.USurvivePotions;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class SporelinkListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();
        PotionHolder holder = EntityMemory.getDataHolder(e, SimplePotionHolder.class);
        if(holder == null) return;

        if(!(holder.getPotion(USurvivePotions.SPORELINK) instanceof SporelinkPotion potion)) return;
        potion.onTakeDamage(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCruxEntityDamage(CruxEntityDamageEvent event) {
        Entity e = event.getEntity();
        PotionHolder holder = EntityMemory.getDataHolder(e, SimplePotionHolder.class);
        if(holder == null) return;

        if(!(holder.getPotion(USurvivePotions.SPORELINK) instanceof SporelinkPotion potion)) return;
        potion.onTakeDamage(event);
    }

}
