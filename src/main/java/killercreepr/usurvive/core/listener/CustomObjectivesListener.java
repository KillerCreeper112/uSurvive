package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.cruxadvancements.api.advancement.CruxAdvancement;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.core.registries.AdvancementRegistries;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.entity.memory.LivingInfernoHolder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CustomObjectivesListener {
    protected final CruxAdvancementManager MANAGER = AdvancementRegistries.ADVANCEMENT_MANAGERS
        .get(USurvivePlugin.inst().key("main"));
    protected final CruxAdvancement TOXIC_SURVIVOR = MANAGER.getAdvancement(Crux.key("toxic_survivor"));

    protected final CruxAdvancement LIVING_INFERNO = MANAGER.getAdvancement(Crux.key("living_inferno"));

    public void onDeath(Entity victim, Entity killer){
        if(!(killer instanceof Player p)) return;
        if(p.getFireTicks() < 1) return;
        if(LIVING_INFERNO == null || LIVING_INFERNO.isGranted(p)) return;
        LivingInfernoHolder holder = EntityMemory.getOrCreateDataHolder(killer, LivingInfernoHolder.class, mem ->{
            LivingInfernoHolder data = new LivingInfernoHolder(mem);
            data.setLastKill(System.currentTimeMillis());
            return data;
        });
        if(holder == null) return;

        holder.setLastKill(System.currentTimeMillis());
        holder.setStreak(holder.getStreak() + 1);
        if(holder.getStreak() < 10) return;
        holder.markForRemoval();
        //todo grant
    }
}
