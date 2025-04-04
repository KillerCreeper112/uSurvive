package killercreepr.usurvive.core.loot;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.usurvive.api.event.PostItemInfusePotionsEvent;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class USurviveEventLootContexts {
    public static LootContext.Builder builder(@NotNull PostItemInfusePotionsEvent event){
        Entity itemHolder = event.getItemHolder();
        Entity owner = EntityDamager.getOwner(itemHolder);
        return builder()
            .info(
                DataExchange.builder()
                    .putAll(event.getPotions(), "potions")
                    .putAll(itemHolder, "item_holder")
                    .putAll(event.getItem(), "item")
                    .build()
            )
            .location(itemHolder.getLocation())
            .looter(owner)
            .looted(itemHolder)
            ;
    }

    private static LootContext.Builder builder(){
        return LootContext.builder();
    }
}
