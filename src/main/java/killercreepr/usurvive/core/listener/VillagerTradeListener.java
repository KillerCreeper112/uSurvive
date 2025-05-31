package killercreepr.usurvive.core.listener;

import io.papermc.paper.event.player.PlayerTradeEvent;
import killercreepr.crux.core.Crux;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.TradeSelectEvent;

public class VillagerTradeListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerTrade(PlayerTradeEvent event) {
        var recipe = event.getTrade();
        if(recipe.getResult().getType() == Material.EMERALD){
            event.setCancelled(true);
            event.getPlayer().sendMessage(Crux.format().deserialize(
                "<red>Buying villager trades have been disabled for the time being. We are experimenting with heavily nerfing villager trading, especially in regards to mending. Feel free to provide feedback on our Discord! /discord"
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
                "<red>Buying villager trades have been disabled for the time being. We are experimenting with heavily nerfing villager trading, especially in regards to mending. Feel free to provide feedback on our Discord! /discord"
            ));
        }
    }


}
