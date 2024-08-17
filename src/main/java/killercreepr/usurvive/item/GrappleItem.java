package killercreepr.usurvive.item;

import killercreepr.crux.data.entity.PlayerMemory;
import killercreepr.crux.item.dynamic.DynamicItem;
import killercreepr.crux.util.CruxItem;
import killercreepr.cruxitems.item.interaction.InteractableItem;
import killercreepr.cruxitems.item.interaction.ItemUseContext;
import killercreepr.cruxitems.item.interaction.ItemUseResult;
import killercreepr.cruxitems.item.plugin.CfgPluginItem;
import killercreepr.usurvive.data.entity.GrapplerHolder;
import killercreepr.usurvive.mechanics.grapple.GrappleHookHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GrappleItem extends CfgPluginItem implements InteractableItem {
    public GrappleItem(@NotNull Key key, @NotNull DynamicItem item) {
        super(key, item);
    }

    @Override
    public @NotNull ItemUseResult onUse(@NotNull ItemUseContext ctx) {
        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();

        GrapplerHolder grappler = PlayerMemory.getOrCreate(p)
            .getPlayerDataHolderOrCompute(GrapplerHolder.class, GrapplerHolder::new);
        if(grappler.getGrapples().size() > 2) return ItemUseResult.empty();

        GrappleHookHolder hook = GrappleHookHolder.createNew(
            p, p.getEyeLocation(), p.getEyeLocation().getDirection()
        );
        grappler.addGrapple(hook);

        return ItemUseResult.cancelled();
    }
}
