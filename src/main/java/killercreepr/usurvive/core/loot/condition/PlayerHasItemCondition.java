package killercreepr.usurvive.core.loot.condition;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.loot.conditions.LootCondition;
import killercreepr.crux.core.loot.conditions.BaseCondition;
import killercreepr.crux.core.util.CruxInv;
import killercreepr.usurvive.core.util.ApplyToItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerHasItemCondition extends BaseCondition {
    protected final @Nullable LootCondition itemCondition;
    protected final int amount;

    public PlayerHasItemCondition(@NotNull String target, @Nullable LootCondition itemCondition, int amount) {
        super(target);
        this.itemCondition = itemCondition;
        this.amount = amount;
    }

    public boolean checkItemCondition(HumanEntity p, LootContext ctx){
        return ApplyToItem.hasItems(
            item -> itemCondition.test(ctx.withInfo(ctx.info().append("this", Holder.directObject(item)))),
            amount, apply -> apply.applyAllItems(p)
        );
    }

    @Override
    public boolean test(@NotNull LootContext ctx) {
        if(!(ctx.info().get(target) instanceof HumanEntity p)) return false;
        if(itemCondition != null){
            if(!checkItemCondition(p, ctx)) return false;
        }
        return true;
    }
}
