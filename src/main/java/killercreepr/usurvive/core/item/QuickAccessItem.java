package killercreepr.usurvive.core.item;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.core.Crux;
import killercreepr.cruxitems.api.item.interaction.*;
import killercreepr.cruxitems.api.item.inventory.InventoryItem;
import killercreepr.cruxitems.api.item.inventory.ItemClickContext;
import killercreepr.cruxitems.core.item.plugin.CfgPluginItem;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.core.data.entity.MemoryPlayerSettingsHolder;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettingSelections;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettings;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class QuickAccessItem extends CfgPluginItem implements InteractableItem, InventoryItem, SwapHandsItem {
    public QuickAccessItem(@NotNull Key key, @NotNull DynamicItem item) {
        super(key, item);
    }

    @Override
    public @NotNull ItemUseResult onUse(@NotNull ItemUseContext ctx) {
        if(ctx.getAction().isLeftClick()) return ItemUseResult.empty();
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
            PlayerSettingsHolder settings = EntityMemory.getOrCreateDataHolder(p, MemoryPlayerSettingsHolder.class);
            if(ctx.getClick() != ClickType.SWAP_OFFHAND && settings != null){
                if(!settings.hasSetting(StandardPlayerSettings.QUICK_ACCESS_ITEM, StandardPlayerSettingSelections.LOCKED)){
                    if(!ctx.getClick().isRightClick()){
                        var type = ctx.getView().getTopInventory().getType();
                        if(type == InventoryType.PLAYER || type == InventoryType.CRAFTING){
                            return ItemUseResult.empty();
                        }
                        return ItemUseResult.cancelled();
                    }
                }
            }
            Crux.scheduler().runTask(() -> p.performCommand("menu"));
        }
        return ItemUseResult.cancelled();
    }

    @Override
    public @NotNull SwapHandsResult onSwapHands(@NotNull SwapHandsContext ctx) {
        return SwapHandsResult.cancelled();
    }
}
