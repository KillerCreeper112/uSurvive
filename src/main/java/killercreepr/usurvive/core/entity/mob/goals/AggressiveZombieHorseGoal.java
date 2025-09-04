package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class AggressiveZombieHorseGoal extends CruxMobGoal implements Listener {

    public AggressiveZombieHorseGoal(@NotNull Mob mob) {
        super(mob);
    }

    public AggressiveZombieHorseGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
    }

    @Override
    public double getDefaultMoveSpeedMultiplier() {
        return target == null ? super.getDefaultMoveSpeedMultiplier() : 1.7D;
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return super.isValidNaturalTarget(target) && isValidNaturalTargetType(target);
    }

    public boolean isValidNaturalTargetType(@NotNull Entity e){
        return e instanceof HumanEntity || e instanceof AbstractVillager || e instanceof IronGolem;
    }
}
