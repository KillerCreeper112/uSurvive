package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.event.CruxEntityDeathEvent;
import killercreepr.crux.core.Crux;
import killercreepr.cruxadvancements.api.advancement.CruxAdvancement;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.core.registries.AdvancementRegistries;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.entity.memory.EchoOfTheAbyssHolder;
import killercreepr.usurvive.core.entity.memory.LivingInfernoHolder;
import killercreepr.usurvive.core.entity.memory.ToxicSurvivorHolder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class CustomObjectivesListener implements Listener {
    protected final Holder<CruxAdvancementManager> MANAGER = () -> AdvancementRegistries.ADVANCEMENT_MANAGERS
        .get(USurvivePlugin.inst().key("main"));
    protected final Holder<CruxAdvancement> TOXIC_SURVIVOR = () -> MANAGER.value().getAdvancement(Crux.key("toxic_survivor"));

    protected final Holder<CruxAdvancement> LIVING_INFERNO = () -> MANAGER.value().getAdvancement(Crux.key("living_inferno"));
    protected final Holder<CruxAdvancement> ECHO_OF_THE_ABYSS = () -> MANAGER.value().getAdvancement(Crux.key("echo_of_the_abyss"));

    protected final Collection<UUID> death = new HashSet<>();
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCruxEntityDeath(CruxEntityDeathEvent event) {
        Entity dmger = event.getDamager();
        if(dmger == null) return;
        if(!(EntityDamager.getOwner(dmger) instanceof Player p)) return;
        death.add(p.getUniqueId());
        onDeath(event.getEntity(), p);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.hasChangedBlock()) return;
        Player p = event.getPlayer();
        if(ECHO_OF_THE_ABYSS.value() == null) return;
        if(EntityMemory.getDataHolder(p, EchoOfTheAbyssHolder.class) != null || ECHO_OF_THE_ABYSS.value().isGranted(p)) return;
        EntityMemory.getOrCreateDataHolder(p, EchoOfTheAbyssHolder.class, mem -> new EchoOfTheAbyssHolder(mem, e ->{
            //todo fun little thing when complete
        }));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        Player p = event.getEntity().getKiller();
        if(p == null) return;
        if(death.contains(p.getUniqueId())){
            death.remove(p.getUniqueId());
            return;
        }
        onDeath(event.getEntity(), p);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        if(event.getNewEffect() == null) return;
        if(event.getNewEffect().getType() == PotionEffectType.POISON){
            if(!(event.getEntity() instanceof Player pp)) return;
            if(TOXIC_SURVIVOR.value() == null || TOXIC_SURVIVOR.value().isGranted(pp)) return;

            EntityMemory.getOrCreateDataHolder(event.getEntity(), ToxicSurvivorHolder.class, mem -> new ToxicSurvivorHolder(mem, e ->{
                MANAGER.value().grantAdvancement(pp, TOXIC_SURVIVOR.value());
            }));
        }
    }


    public void onDeath(Entity victim, Entity killer){
        if(!(killer instanceof Player p)) return;
        if(p.getFireTicks() < 1) return;
        if(LIVING_INFERNO.value() == null || LIVING_INFERNO.value().isGranted(p)) return;
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
        MANAGER.value().grantAdvancement(p, LIVING_INFERNO.value());
    }
}
