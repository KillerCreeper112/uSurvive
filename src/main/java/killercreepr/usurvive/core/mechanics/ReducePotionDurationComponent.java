package killercreepr.usurvive.core.mechanics;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.cruxitems.api.item.component.ConsumableComponent;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.consume.ItemConsumeContext;
import killercreepr.cruxitems.api.item.consume.ItemConsumeResult;
import killercreepr.cruxitems.api.item.context.PlayerItemedContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReducePotionDurationComponent implements InteractableComponent, ConsumableComponent {
    public ReducePotionDurationComponent(List<Value> removePotions) {
        this.removePotions = removePotions;
    }

    public record Value(PotionEffectType type, int removeDuration){
    }

    protected final List<Value> removePotions;

    public List<Value> getRemovePotions() {
        return removePotions;
    }

    @Override
    public @NotNull ItemUseResult onInteract(@NotNull ItemUseContext ctx) {
        if(ctx.getEntityClicked() != null && ctx.getAction().isLeftClick()) return ItemUseResult.empty();
        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();
        spit(p, item, ctx);
        return ItemUseResult.cancelled();
    }

    public boolean spit(Player p, CruxItem item,
                        PlayerItemedContext ctx){
        removePotions.forEach(value ->{
            PotionEffect effect = p.getPotionEffect(value.type());
            if(effect == null || effect.isInfinite()) return;
            int newDuration = effect.getDuration() - value.removeDuration();
            p.removePotionEffect(value.type());
            if(newDuration < 1) return;
            p.addPotionEffect(effect.withDuration(newDuration));
        });
        return true;
    }

    @Override
    public boolean isInteractable(@NotNull ItemUseContext ctx) {
        if(ctx.getAction() == Action.PHYSICAL) return false;
        CruxItem item = ctx.getItem();
        if(item.item().getData(DataComponentTypes.CONSUMABLE) != null) return false;
        return true;
    }

    @Override
    public @NotNull ItemConsumeResult onConsume(@NotNull ItemConsumeContext ctx) {
        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();
        spit(p, item, ctx);
        return ItemConsumeResult.empty();
    }

    @Override
    public boolean isConsumable(@NotNull ItemConsumeContext ctx) {
        return true;
    }
}
