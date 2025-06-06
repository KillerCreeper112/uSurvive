package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import io.papermc.paper.event.player.PlayerTradeEvent;
import killercreepr.crux.core.Crux;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.MerchantInventory;

import java.util.UUID;

public class VillagerTradeListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerTrade(PlayerTradeEvent event) {
        var recipe = event.getTrade();
        if(recipe.getResult().getType() == Material.EMERALD){
            event.setCancelled(true);
            event.getPlayer().sendMessage(Crux.format().deserialize(
                "<red>Buying villager trades have been disabled for the time being. We are experimenting with heavily nerfing villager trading. Feel free to provide feedback on our Discord! /discord"
            ));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onTradeSelect(TradeSelectEvent event) {
        var recipe = event.getMerchant().getRecipe(event.getIndex());
        if(recipe.getResult().getType() == Material.EMERALD){
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(Crux.format().deserialize(
                "<red>Buying villager trades have been disabled for the time being. We are experimenting with heavily nerfing villager trading. Feel free to provide feedback on our Discord! /discord"
            ));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if(!(event.getInventory() instanceof MerchantInventory inv)) return;
        if(inv.getMerchant() instanceof Villager villager){
            UUID uuid = event.getPlayer().getUniqueId();
            Reputation reputation = villager.getReputation(uuid);
            if(reputation == null) return;
            reputation.setReputation(ReputationType.MAJOR_POSITIVE, 0);
            reputation.setReputation(ReputationType.MINOR_POSITIVE, 0);
            villager.setReputation(uuid, reputation);
        }
    }

}
