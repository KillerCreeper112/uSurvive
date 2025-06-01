package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.usurvive.api.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.api.mechanics.grapple.GrappleHook;
import killercreepr.usurvive.core.data.entity.GrapplerHolder;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class GrappleListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getPlayer();
        GrapplerHolder grappler = EntityMemory.getDataHolder(p, GrapplerHolder.class);
        if(grappler==null) return;
        grappler.reset(p);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player p = event.getPlayer();
        GrapplerHolder grappler = EntityMemory.getDataHolder(p, GrapplerHolder.class);
        if(grappler==null) return;
        grappler.reset(p);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityUnleash(EntityUnleashEvent event) {
        Entity entity = event.getEntity();
        if(CruxMob.is(entity, USurviveMobs.GRAPPLE_HOOK)) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(CruxMob.is(event.getDamager(), USurviveMobs.GRAPPLE_HOOK)) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity p = event.getWhoClicked();
        if(p.getInventory().getHeldItemSlot() == event.getHotbarButton() || event.getSlot() == p.getInventory().getHeldItemSlot()){
            GrapplerHolder grappler = EntityMemory.getDataHolder(p, GrapplerHolder.class);
            if(grappler==null) return;
            grappler.reset(p);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player p = event.getPlayer();
        GrapplerHolder grappler = EntityMemory.getDataHolder(p, GrapplerHolder.class);
        if(grappler==null) return;
        boolean had = false;
        for (ActiveGrappleHook value : grappler.getGrapples().values()) {
            if(value.hasSwapHandAction()){
                had = true;
                value.onSwapHands(event);
            }
        }
        if(had) return;
        grappler.reset(p);
    }


}
