package killercreepr.usurvive.core.loot.condition;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.text.context.InputContext;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.loot.conditions.BaseCondition;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityHasRankCondition extends BaseCondition {
    protected final NumberProvider rank;
    public EntityHasRankCondition(@NotNull String target, NumberProvider rank) {
        super(target);
        this.rank = rank;
    }

    @Override
    public boolean test(@NotNull LootContext ctx) {
        Entity entity = ctx.info().get(target, Entity.class);
        if(entity == null) return false;

        var inputCtx = InputContext.inputContext(TagContainer.merged().hookAllWithPrefix(ctx.info()));
        if(rank != null){
            int check = rank.sample(inputCtx).intValue();
            if(!entity.hasPermission("usurvive.rank." + check)) return false;
        }

        return true;
    }
}
