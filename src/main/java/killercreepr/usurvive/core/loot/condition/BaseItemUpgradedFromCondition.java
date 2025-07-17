package killercreepr.usurvive.core.loot.condition;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.core.loot.conditions.BaseCondition;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.component.impl.BaseItemUpgradedFrom;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseItemUpgradedFromCondition extends BaseCondition {
    protected final @Nullable String type;
    public BaseItemUpgradedFromCondition(@NotNull String target, @Nullable String type) {
        super(target);
        this.type = type;
    }

    @Override
    public boolean test(@NotNull LootContext ctx) {
        ItemStack item = ctx.info().get(target, ItemStack.class);
        if(item == null && ctx.info().get(target) instanceof Item it) item = it.getItemStack();
        if(item==null) return false;
        BaseItemUpgradedFrom from = CruxItem.wrap(item).getOrDefaultData(USurviveComponents.BASE_ITEM_UPGRADED_FROM);
        if(from == null) return false;
        if(type != null){
            if(!type.equalsIgnoreCase(from.getType())) return false;
        }
        return true;
    }
}
