package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalPath;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NPCControlGoal extends CruxMobModeledGoal implements PathTargetMobGoal {
    protected final PathTargetMobGoal pathTarget = PathTargetMobGoal.pathTargetMobGoal(this, 1.1);
    public NPCControlGoal(@NotNull Mob mob) {
        super(mob);
    }

    public NPCControlGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
    }

    public boolean tickPath = false;
    public boolean tick = true;
    public boolean naturalTarget = false;

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return naturalTarget && super.isValidNaturalTarget(target);
    }

    @Override
    public @Nullable GoalPath getPath() {
        return pathTarget.getPath();
    }

    @Override
    public void setPath(@Nullable GoalPath goalPath) {
        pathTarget.setPath(goalPath);
    }

    @Override
    public void tick() {
        if(!tick) return;
        super.tick();
        if(target != null) return;
        if(tickPath) this.pathTarget.tick();
    }
}
