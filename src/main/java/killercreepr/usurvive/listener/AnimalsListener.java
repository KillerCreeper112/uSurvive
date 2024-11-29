package killercreepr.usurvive.listener;

import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.usurvive.entity.mob.goals.animal.AnimalEnhancedGoal;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.jetbrains.annotations.NotNull;

public class AnimalsListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for(Entity e : event.getEntities()){
            if(!(e instanceof Animals a)) continue;
            loadAnimal(a);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity e = event.getEntity();
        if(!(e instanceof Animals a)) return;
        loadAnimal(a);
    }

    public void loadAnimal(@NotNull Animals a){
        /*CruxMob mob = NotOkayMobs.getVanilla(a.getClass());
        if(mob != null){
            mob.load(a);
            return;
        }*/

        CruxGoalUtil.addIfNotPresent(a, AnimalEnhancedGoal.class, 2, () -> new AnimalEnhancedGoal(a));
        /*if(Bukkit.getMobGoals().getGoal(a, AnimalEnhancedGoal.KEY) != null) return;
        Bukkit.getMobGoals().addGoal(a, 2, new AnimalEnhancedGoal(a));*/
    }
}
