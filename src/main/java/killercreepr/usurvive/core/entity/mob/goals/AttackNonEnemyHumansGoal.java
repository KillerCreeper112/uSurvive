package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.location.DynamicLocation;
import killercreepr.crux.core.location.EntityLocation;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.crux.core.util.GetNear;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

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

    public @Nullable Entity findNPCTarget(@Nullable Predicate<Entity> targetCheck, double followRange) {
        DynamicLocation loc = EntityLocation.createEntity(this.mob);
        List<LivingEntity> targets = new GetEntityNear<>(loc, LivingEntity.class)
            .range(followRange)
            .operation(GetNear.Operation.NEAREST)
            .filter((ex) -> this.isValidNaturalTarget(ex) && (targetCheck == null || targetCheck.test(ex)))
            .find();

        if (!targets.isEmpty()) {
            return (Entity)targets.getFirst();
        } else {
            return null;
        }
    }

    protected byte tick = 0;
    @Override
    public void tick() {
        super.tick();
        tick++;
        if(tick % 8 != 0) return;
        tickExtraLogic();
    }

    public void tickExtraLogic(){
        if(target == null) return;

        double distance = getSquaredDistanceFromTarget();
        if(distance < 10*10) return;

        if(findNPCTarget(
            e -> !(e instanceof Player),
            6D
        ) instanceof LivingEntity target){
            setTarget(target);
        }
    }
}
