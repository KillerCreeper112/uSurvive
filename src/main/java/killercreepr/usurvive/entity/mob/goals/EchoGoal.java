package killercreepr.usurvive.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.util.CruxTag;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.usurvive.USurvivePlugin;
import killercreepr.usurvive.death.DeathManager;
import killercreepr.usurvive.death.PlayerDeath;
import org.bukkit.Chunk;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class EchoGoal extends CruxMobGoal {
    public EchoGoal(@NotNull Mob mob) {
        this(CruxMobGoal.defaultKey(), mob);
    }

    public EchoGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
        exp = CruxTag.get(mob, "exp", PersistentDataType.INTEGER, null);
    }
    protected Integer exp;

    @Override
    public void tick() {
        //this.checkTargetLogic(); is not aggressive without
        this.targetLogic();
        this.sound();
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        if(exp == null) exp = getPlayerDeath().getExperience();

        exp--;
        spawnExp(exp);
        CruxTag.set(mob, "exp", PersistentDataType.INTEGER, exp);
    }

    public PlayerDeath getPlayerDeath(){
        DeathManager deathManager = USurvivePlugin.inst().getDeathManager();
        PlayerDeath death = deathManager.getEchoToPlayerDeath().get(mob.getUniqueId());
        if(death == null) throw new UnsupportedOperationException(mob.getUniqueId() + " does not have a set player death for it!");
        return death;
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        super.onEntityDeath(event);
        DeathManager deathManager = USurvivePlugin.inst().getDeathManager();
        PlayerDeath death = getPlayerDeath();
        deathManager.getDeaths().remove(
            death.getWorldUUID(),
            Chunk.getChunkKey(death.getPosition().toLocation(null)),
            death.getPosition()
        );

        death.getDrops().forEach(drop ->{
            mob.getWorld().dropItem(mob.getLocation(), drop);
        });
        spawnExp(exp==null?death.getExperience():exp);
    }

    public void spawnExp(int value){
        if(value < 1) return;
        mob.getWorld().spawn(mob.getLocation(), ExperienceOrb.class, e ->{
            e.setExperience(value);
        });
    }
}
