package killercreepr.usurvive.core.menu.action;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxadvancements.core.entity.memory.AdvancementHolder;
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.cruxmenus.api.menu.contex.ActionContext;
import killercreepr.cruxmenus.core.menu.action.SimpleMenuAction;
import killercreepr.usurvive.core.advancement.objective.GuideShardChooseObjective;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.key.Key;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GuideShardAction extends SimpleMenuAction {
    public GuideShardAction(@NotNull Key key) {
        super(key);
    }

    @Override
    public boolean execute(@NotNull ActionContext ctx, @NotNull String[] args) {
        HumanEntity p = ctx.getPlayer();

        Key removeItem = Crux.key(ctx.parserCtx().deserializeString(args[0]));
        ItemStack found = findItem(removeItem, p.getInventory());
        if(found == null){
            return true;
        }
        found.setAmount(found.getAmount()-1);

        DynamicItem item = DynamicItem.builder().material(args[1]).build();
        ItemStack built = item.buildItem(ctx.parserCtx());
        if(!CruxItem.isEmpty(built)){
            CruxEntityUtil.giveOrDrop(p, built);
            Lang.GUIDE_SHARD_USE.use(
                p, TagContainer.merged().hook(p).hook(built)
            );
        }
        new ParticleBuilder(Particle.TOTEM_OF_UNDYING)
            .location(p.getLocation().add(0, p.getHeight()/2, 0))
            .extra(.05)
            .offset(.4, .4, .4)
            .count(CruxMath.random(9, 12))
            .spawn();
        CreateSound.sound(Sound.ITEM_TOTEM_USE, 1.8f).playAt(p);

        AdvancementHolder holder = holder(p);
        if(holder!= null) {
            holder.getAdvancementTracker().apply(GuideShardChooseObjective.class, (manager, advancement, objective) -> {
                objective.trigger(p.getUniqueId(), manager, advancement, LootContext.builder().looter(p).build());
            });
        }
        return true;
    }

    private AdvancementHolder holder(@NotNull Entity p){
        return AdvancementHolder.holderIfLoaded(p);
    }

    public ItemStack findItem(Key key, Inventory inventory){
        for(ItemStack item : inventory){
            if(CruxItem.isEmpty(item)) continue;
            if(!PluginItem.isPluginItem(item, key)) continue;
            return item;
        }
        return null;
    }
}
