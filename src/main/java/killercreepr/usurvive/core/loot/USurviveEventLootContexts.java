package killercreepr.usurvive.core.loot;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.usurvive.api.dialog.DialogTalker;
import killercreepr.usurvive.api.event.*;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class USurviveEventLootContexts {
    public static LootContext.Builder builder(@NotNull DialogNaturallyEndEvent event){
        return builder((DialogEvent) event);
    }
    public static LootContext.Builder builder(@NotNull DialogStartEvent event){
        return builder((DialogEvent) event);
    }
    public static LootContext.Builder builder(@NotNull DialogSendContentInfoEvent event){
        return builder((DialogEvent) event)
          .edit(builder ->{
              builder.info(
                builder.info().append(event.getInfo())
                  .append("event_info", Holder.direct(event.getInfo()))
              );
          });
    }
    private static LootContext.Builder builder(@NotNull DialogEvent event){
        var talker = event.getDialogPlayer() instanceof DialogTalker t ? t.getTalker() : null;
        Entity mainTarget = null;
        for (Audience audience : event.getDialogPlayer().audience()) {
            if(audience instanceof Entity e){
                mainTarget = e;
                break;
            }
        }
        return builder()
          .info(
            DataExchange.builder()
              .putAll(event.getDialogPlayer(), "dialog_player")
              .putAll(event.getDialogContent(), "dialog_content")
              .putAll(talker, "talker")
              .build()
          )
          .location(talker == null ? (mainTarget == null ? null : mainTarget.getLocation()) : talker.getLocation())
          .looter(mainTarget)
          .looted(talker == null ? event.getDialogPlayer() : talker)
          ;
    }
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
