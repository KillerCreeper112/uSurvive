package killercreepr.usurvive.item;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.cruxitems.api.item.interaction.InteractableItem;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.cruxitems.core.item.plugin.CfgPluginItem;
import killercreepr.usurvive.data.entity.GrapplerHolder;
import killercreepr.usurvive.lang.Lang;
import killercreepr.usurvive.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.mechanics.grapple.Grapple;
import killercreepr.usurvive.mechanics.grapple.GrappleContext;
import killercreepr.usurvive.mechanics.grapple.GrappleHook;
import killercreepr.usurvive.mechanics.grapple.standard.StandardGrappleHooks;
import killercreepr.usurvive.persistence.USurvivePersist;
import killercreepr.usurvive.registries.USurviveRegistries;
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

        Grapple hookData = USurvivePersist.GRAPPLE.get(item.item(), null);
        GrappleHook hook;
        int maxGrapples;
        if(hookData == null){
            hook = StandardGrappleHooks.PUFFERFISH;
            maxGrapples = hook.defaultMaxHooks();
        }else{
            hook = USurviveRegistries.GRAPPLE_HOOK.get(hookData.getHook());
            maxGrapples = hookData.getMaxHooks();
        }
        if(hook==null || maxGrapples < 1) return ItemUseResult.empty();

        GrapplerHolder grappler = PlayerMemory.getOrCreate(p)
            .getPlayerDataHolderOrCompute(GrapplerHolder.class, GrapplerHolder::new);

        int id;
        if(maxGrapples > 1) id = ctx.getAction().isRightClick() ? 0 : 1;
        else id = 0;

        ActiveGrappleHook previous = grappler.getGrapple(id);
        if(previous != null){
            previous.remove();
            return ItemUseResult.cancelled();
        }
        grappler.throwNewHook(hook, new GrappleContext(
            p.getEyeLocation(), p, id, 1D
        ));
        Lang.GRAPPLE_USE.playAt(p.getLocation());
        return ItemUseResult.cancelled();
    }
}
