package killercreepr.usurvive.core.menu.action;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.cruxmenus.api.menu.contex.ActionContext;
import killercreepr.cruxmenus.core.menu.action.SimpleMenuAction;
import net.kyori.adventure.key.Key;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
        if(!CruxItem.isEmpty(built)) CruxEntityUtil.giveOrDrop(p, built);
        new ParticleBuilder(Particle.TOTEM_OF_UNDYING)
            .location(p.getLocation().add(0, p.getHeight()/2, 0))
            .extra(.05)
            .offset(.4, .4, .4)
            .count(CruxMath.random(9, 12))
            .spawn();
        CreateSound.sound(Sound.ITEM_TOTEM_USE, 1.8f).playAt(p);
        return true;
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
