package killercreepr.usurvive.core.loot.condition;

import killercreepr.crux.api.entity.predicate.EntityPredicate;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.core.loot.conditions.BaseCondition;
import killercreepr.usurvive.api.dialog.listener.DialogListener;
import killercreepr.usurvive.core.entity.memory.EntityDialogerHolder;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityDialogerHasDialogCondition extends BaseCondition {
    protected final EntityPredicate talker;
    public EntityDialogerHasDialogCondition(@NotNull String target, EntityPredicate talker) {
        super(target);
      this.talker = talker;
    }

    @Override
    public boolean test(@NotNull LootContext ctx) {
        Entity entity = ctx.info().get(target, Entity.class);
        if(entity == null) return false;

        var holder = EntityDialogerHolder.holder(entity);
        if(holder == null) return false;
        if(holder.getEntries().isEmpty()) return false;


        if(talker != null){
            boolean has = false;
            for (EntityDialogerHolder.Entry entry : holder.getEntries().values()) {
                var e = entry.entity.get();
                if(e == null) continue;
                if(talker.test(e)){
                    has = true;
                    break;
                }
            }
            if(!has) return false;
        }

        return true;
    }
}
