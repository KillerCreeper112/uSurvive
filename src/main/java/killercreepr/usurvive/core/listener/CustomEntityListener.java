package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import killercreepr.cruxblocks.api.event.CustomEntityExplodeEvent;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.usurvive.core.entity.USurviveMobs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class CustomEntityListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCustomEntityExplode(CustomEntityExplodeEvent event) {
        if(CruxMob.is(event.getEntity(), USurviveMobs.GUST_TNT)){
            event.clearBlocks();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if(CruxMob.is(event.getRemover(), USurviveMobs.GUST_TNT)){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
        var dmger = event.getHitBy();
        //var victim = event.getEntity();
        if(CruxMob.is(dmger, USurviveMobs.GUST_TNT)){
            event.setKnockback(event.getKnockback().multiply(2.2f));
        }
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        var dmger = event.getDamager();
        var victim = event.getEntity();
        if(victim instanceof ArmorStand || !(victim instanceof LivingEntity)){
            if(CruxMob.is(dmger, USurviveMobs.GUST_TNT)){
                event.setDamage(0D);
            }
        }else{
            if(CruxMob.is(dmger, USurviveMobs.GUST_TNT)){
                event.setDamage(event.getDamage()*0.5D);
            }
        }
    }

}
