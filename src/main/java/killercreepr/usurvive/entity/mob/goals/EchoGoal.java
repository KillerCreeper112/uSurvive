package killercreepr.usurvive.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.util.CruxTag;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.usurvive.USurvivePlugin;
import killercreepr.usurvive.death.DeathManager;
import killercreepr.usurvive.death.PlayerDeath;
import org.bukkit.Chunk;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
    protected Integer expToDropEachHit;

    @Override
    public void tick() {
        //this.checkTargetLogic(); is not aggressive without
        this.targetLogic();
        this.sound();
    }

    public static int calculateExpToDropEachHit(double damageDealt, double mobHealth, int totalExp) {
        // Calculate the hitPercentage based on damage dealt
        double hitPercentage = damageDealt / mobHealth;

        // Calculate expToDropEachHit based on hitPercentage and totalExp
        int expToDropEachHit = (int) Math.ceil(hitPercentage * totalExp); // Use Math.ceil to ensure we don't drop zero experience

        // Ensure expToDropEachHit does not exceed remaining totalExp
        expToDropEachHit = Math.min(expToDropEachHit, totalExp);

        return expToDropEachHit;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        if(exp == null){
            exp = getPlayerDeath().getExperience();
        }

        if(exp < 1) return;

        expToDropEachHit = calculateExpToDropEachHit(event.getFinalDamage(), mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), exp);

        exp = Math.max(exp - expToDropEachHit, 0);
        spawnExp(expToDropEachHit);
        CruxTag.set(mob, "exp", PersistentDataType.INTEGER, exp);
    }

    public PlayerDeath getPlayerDeath(){
        DeathManager deathManager = USurvivePlugin.inst().getDeathManager();
        PlayerDeath death = deathManager.getEchoToPlayerDeath().get(mob.getUniqueId());
        if(death == null) throw new UnsupportedOperationException(mob.getUniqueId() + " does not have a set player death for it!");
        return death;
    }

    @Override
    protected void onRemovalOrDeath(boolean died) {
        super.onRemovalOrDeath(died);
        if(!died) return;
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
    @EventHandler(
        ignoreCancelled = true,
        priority = EventPriority.MONITOR
    )
    public void death(EntityDeathEvent event) {
        if (!event.getEntity().equals(this.mob)) return;
        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    public void spawnExp(int value){
        if(value < 1) return;
        mob.getWorld().spawn(mob.getLocation(), ExperienceOrb.class, e ->{
            e.setCount(1);
            e.setExperience(value);
        });
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public Integer getExpToDropEachHit() {
        return expToDropEachHit;
    }

    public void setExpToDropEachHit(Integer expToDropEachHit) {
        this.expToDropEachHit = expToDropEachHit;
    }
}
