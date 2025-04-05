package killercreepr.usurvive.core.loot;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.usurvive.api.event.EntityDeepSlumberEvent;
import killercreepr.usurvive.api.event.EntityLeaveSlumberEvent;
import killercreepr.usurvive.api.event.EntitySlumberEvent;
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
    public static LootContext.Builder builder(@NotNull EntityLeaveSlumberEvent event){
        Entity entity = event.getEntity();
        return builder()
            .location(entity.getLocation())
            .looter(entity)
            .looted(event.getSlumber())
            ;
    }
    public static LootContext.Builder builder(@NotNull EntitySlumberEvent event){
        Entity entity = event.getEntity();
        return builder()
            .location(entity.getLocation())
            .looter(entity)
            .looted(event.getSlumber())
            ;
    }
    public static LootContext.Builder builder(@NotNull EntityDeepSlumberEvent event){
        Entity entity = event.getEntity();
        return builder()
            .location(entity.getLocation())
            .looter(entity)
            .looted(event.getSlumber())
            ;
    }

    private static LootContext.Builder builder(){
        return LootContext.builder();
    }
}
