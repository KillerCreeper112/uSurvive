package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.usurvive.core.component.USurviveComponents;
import org.bukkit.*;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class WaxPaintingListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(!(event.getRightClicked() instanceof Painting painting)) return;
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if(CruxItem.isEmpty(item)) return;
        if(item.getType() == Material.HONEYCOMB){
            var crux = CruxEntity.entity(painting);
            if(crux.getOrDefault(USurviveComponents.WAXED, false)) return;

            if(p.getGameMode() != GameMode.CREATIVE) item.setAmount(item.getAmount()-1);
            crux.set(USurviveComponents.WAXED, true);

            p.playEffect(painting.getLocation(), Effect.COPPER_WAX_ON, null);
            CreateSound.sound(Sound.ITEM_HONEYCOMB_WAX_ON, 0.5f, 1f).playAt(painting.getLocation());
        }
    }
}
