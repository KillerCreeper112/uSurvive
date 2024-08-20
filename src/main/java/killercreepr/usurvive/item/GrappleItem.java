package killercreepr.usurvive.item;

import killercreepr.crux.data.entity.PlayerMemory;
import killercreepr.crux.item.dynamic.DynamicItem;
import killercreepr.crux.util.CruxItem;
import killercreepr.cruxitems.item.interaction.InteractableItem;
import killercreepr.cruxitems.item.interaction.ItemUseContext;
import killercreepr.cruxitems.item.interaction.ItemUseResult;
import killercreepr.cruxitems.item.plugin.CfgPluginItem;
import killercreepr.usurvive.data.entity.GrapplerHolder;
import killercreepr.usurvive.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.mechanics.grapple.Grapple;
import killercreepr.usurvive.mechanics.grapple.GrappleContext;
import killercreepr.usurvive.mechanics.grapple.GrappleHook;
import killercreepr.usurvive.mechanics.grapple.standard.StandardGrappleHooks;
import killercreepr.usurvive.persistence.USurvivePersist;
import killercreepr.usurvive.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        if(hookData == null){
            hook = StandardGrappleHooks.PUFFERFISH;
        }else{
            hook = USurviveRegistries.GRAPPLE_HOOK.get(hookData.getGrappleHook());
        }
        if(hook==null) return ItemUseResult.empty();

        GrapplerHolder grappler = PlayerMemory.getOrCreate(p)
            .getPlayerDataHolderOrCompute(GrapplerHolder.class, mem ->{
                GrapplerHolder holder = new GrapplerHolder(mem);
                holder.setMaxGrapples(2);
                return holder;
            });

        int id = ctx.getAction().isRightClick() ? 0 : 1;
        ActiveGrappleHook previous = grappler.getGrapple(id);
        if(previous != null){
            previous.remove();
            return ItemUseResult.cancelled();
        }
        ActiveGrappleHook active = grappler.throwNewHook(hook, new GrappleContext(
            p.getEyeLocation(), p, id, 1D
        ));
        grappler.putGrapple(active);

        return ItemUseResult.cancelled();
    }

    public int getMaxGrapples(@NotNull ItemStack item){
        return 3;
    }
}
