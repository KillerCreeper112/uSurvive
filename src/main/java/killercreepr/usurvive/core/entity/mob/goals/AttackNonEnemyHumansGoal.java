package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.core.Crux;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class AttackNonEnemyHumansGoal extends CruxMobGoal {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("attack_non_enemy_humans"));

    public AttackNonEnemyHumansGoal(@NotNull Mob mob) {
        this(KEY, mob);
    }

    public AttackNonEnemyHumansGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
    }
    public double getForgetTargetDistance() {
        return getFollowDistance() * 2D;
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        if(!CruxMob.isInCategory(target, MobCategory.HUMAN)) return false;
        if(CruxMob.isInCategory(target, MobCategory.ENEMY)) return false;
        return super.isValidNaturalTarget(target);
    }
}
