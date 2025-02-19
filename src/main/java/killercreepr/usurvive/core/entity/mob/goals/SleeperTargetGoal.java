package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import killercreepr.crux.core.location.DynamicLocation;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;

public class SleeperTargetGoal extends CruxMobGoal {
    public SleeperTargetGoal(@NotNull Mob mob) {
        this(defaultKey(), mob);
    }

    public SleeperTargetGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
        findTargetCooldown = CruxMath.random(20, 40);
    }

    @Override
    public void tick() {
        if(findTargetCooldown > 0){
            findTargetCooldown--;
            return;
        }
        findTargetCooldown = CruxMath.random(60, 100);
        new GetEntityNear<>(DynamicLocation.createEntity(mob), Mob.class)
            .range(16D)
            .filter(m -> !m.equals(mob) && CruxMob.isInCategory(m, MobCategory.ENEMY))
            .find().forEach(enemy ->{
                if(enemy.getTarget() != null) return;
                if(!enemy.hasLineOfSight(mob)) return;
                enemy.setTarget(mob);
            });
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityPathfind(EntityPathfindEvent event){
        if(!event.getEntity().equals(mob)) return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event){
        if(!event.getEntity().equals(mob)) return;
        if(event.getTarget() == null) return;
        event.setCancelled(true);
    }
}
