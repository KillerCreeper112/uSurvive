package killercreepr.usurvive.item;

import killercreepr.crux.item.dynamic.DynamicItem;
import killercreepr.cruxitems.item.interaction.InteractableItem;
import killercreepr.cruxitems.item.interaction.ItemUseContext;
import killercreepr.cruxitems.item.interaction.ItemUseResult;
import killercreepr.cruxitems.item.inventory.InventoryItem;
import killercreepr.cruxitems.item.inventory.ItemClickContext;
import killercreepr.cruxitems.item.plugin.CfgPluginItem;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuickAccessItem extends CfgPluginItem implements InteractableItem, InventoryItem {
    public QuickAccessItem(@NotNull Key key, @NotNull DynamicItem item) {
        super(key, item);
    }

    @Override
    public @NotNull ItemUseResult onUse(@NotNull ItemUseContext ctx) {
        Player p = ctx.getPlayer();
        switch (p.getGameMode()){
            case SPECTATOR ->{
                return ItemUseResult.empty();
            }
        }
        p.performCommand("menu");
        return ItemUseResult.cancelled();
    }

    @Override
    public @NotNull ItemUseResult onClick(@NotNull ItemClickContext ctx) {
        if(ctx.getWhoClicked() instanceof Player p){
            switch (p.getGameMode()){
                case CREATIVE, SPECTATOR ->{
                    return ItemUseResult.empty();
                }
            }
            p.performCommand("menu");
        }
        return ItemUseResult.cancelled();
    }
}
