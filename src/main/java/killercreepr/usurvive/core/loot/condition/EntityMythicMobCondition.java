package killercreepr.usurvive.core.loot.condition;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.core.loot.conditions.BaseCondition;
import killercreepr.crux.core.util.CruxTag;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class EntityMythicMobCondition extends BaseCondition {
    protected final String id;
    public EntityMythicMobCondition(@NotNull String target, String id) {
        super(target);
        this.id = id;
    }

    @Override
    public boolean test(@NotNull LootContext ctx) {
        Entity entity = ctx.info().get(target, Entity.class);
        if(entity == null) return false;

        if(id != null){
            if(!id.equalsIgnoreCase(CruxTag.get(entity, "mythicmobs:type", PersistentDataType.STRING, null))) return false;
        }else if(!CruxTag.has(entity, "mythicmobs:type")) return false;


        return true;
    }
}
