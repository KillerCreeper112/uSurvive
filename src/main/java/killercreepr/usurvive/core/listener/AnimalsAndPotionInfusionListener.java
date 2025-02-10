package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.usurvive.api.component.PotionInfusable;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.entity.memory.GoldenItemCauldronHolder;
import killercreepr.usurvive.core.entity.mob.goals.animal.AnimalEnhancedGoal;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.jetbrains.annotations.NotNull;

public class AnimalsAndPotionInfusionListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for(Entity e : event.getEntities()){
            if(!(e instanceof Animals a)) continue;
            loadAnimal(a);
        }
    }

    public void onItemSpawn(Item item){
        CruxItem crux = CruxItem.wrap(item.getItemStack());
        PotionInfusable infused = crux.getOrDefaultData(USurviveComponents.POTION_INFUSABLE);
        if(infused == null || infused.getMaxInfusions() < 1) return;

        EntityMemory.getOrCreateDataHolder(item, GoldenItemCauldronHolder.class, GoldenItemCauldronHolder::new);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity e = event.getEntity();
        if(e instanceof Item ee){
            onItemSpawn(ee);
            return;
        }
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
