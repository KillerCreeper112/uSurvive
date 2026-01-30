package killercreepr.usurvive.core.loot.condition;

import com.destroystokyo.paper.entity.ai.Goal;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.loot.conditions.BaseCondition;
import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class MobHasPathTargetCondition extends BaseCondition {
    public MobHasPathTargetCondition(@NotNull String target) {
        super(target);
    }

    @Override
    public boolean test(@NotNull LootContext ctx) {
        Entity entity = ctx.info().get(target, Entity.class);
        if(entity == null) return false;
        if(!(entity instanceof Mob mob)) return false;

        for (Goal<Mob> allGoal : Crux.getServer().getMobGoals().getAllGoals(mob)) {
            if(allGoal instanceof PathTargetMobGoal g){
                if(g.hasPath()) return true;
            }
        }

        return false;
    }
}
