package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalNode;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalPath;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class NPCControlGoal extends CruxMobGoal implements PathTargetMobGoal {
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

    public List<GoalNode> buildingPathNodes = new ArrayList<>();

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

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.LOOK, GoalType.TARGET, GoalType.MOVE, GoalType.JUMP);
    }
}
