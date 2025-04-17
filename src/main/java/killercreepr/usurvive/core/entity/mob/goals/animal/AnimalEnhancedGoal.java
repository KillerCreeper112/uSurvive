package killercreepr.usurvive.core.entity.mob.goals.animal;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.api.data.holder.LocationHolder;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.location.DynamicLocation;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AnimalEnhancedGoal extends CruxMobGoal implements Listener {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("animal_enhanced"));
    public AnimalEnhancedGoal(@NotNull Mob mob) {
        super(KEY, mob);
    }

    public AnimalEnhancedGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
    }

    @Override
    public void start() {
        super.start();
        if(CruxAttribute.hasAttributeData(mob)) return;
        double width = mob.getWidth();
        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(
            CruxMath.random(.5D, 1D) + width
        ));

        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(
            CruxMath.random(0D, .25D)
        ));

        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(
            CruxMath.random(-20D, -6D)
        ));

        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(
            CruxMath.random(5D, 10D)
        ));

        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(
            CruxMath.random(2D, 8D)
        ));

        CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.baseModifier(
            mob.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue()
        ));
    }

    public boolean testAggression(@NotNull LivingEntity target){
        return CruxMath.testChance(28D);
    }

    public boolean testPersistentAnger(@NotNull LivingEntity target){
        return CruxMath.testChance(25D);
    }

    protected final Key SPEED_ATTRIBUTE_ANGER_KEY = Crux.key("anger");
    @Override
    public void tick() {
        //this.checkTargetLogic(); Animals do not actively search for a fight.
        this.targetLogic();
        this.sound();

        if(target == null){
            CruxAttribute.removeModifier(mob, CruxAttribute.MOVEMENT_SPEED, SPEED_ATTRIBUTE_ANGER_KEY);
        }else{
            if(CruxAttribute.getModifier(mob, CruxAttribute.MOVEMENT_SPEED, SPEED_ATTRIBUTE_ANGER_KEY) == null){
                CruxAttribute.addModifier( mob, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.modifier(
                    SPEED_ATTRIBUTE_ANGER_KEY,
                    CruxMath.random(.2D, .6D), CruxAttribute.Operation.MULTIPLY
                ));
            }
        }

        double moveSpeed = CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED);
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(moveSpeed);
    }

    public void panic(@NotNull LocationHolder from){
        AnimalEnhancedPanicGoal goal = CruxGoalUtil.getOrAddGoal(mob, AnimalEnhancedPanicGoal.class, 1);
        goal.panic(from, CruxMath.random(50, 160));
    }

    public @NotNull Collection<Animals> getNearbyAnimals(){
        return new GetEntityNear<>(DynamicLocation.from(mob), Animals.class)
            .range(16D)
            .find();
        //return CruxEntityUtil.getEntitiesNear(Animals.class, mob.getLocation(), 16D, null);
    }

    public void onDeath(@NotNull LivingEntity killer) {
        for(Mob m : getNearbyAnimals()){
            if(!(Bukkit.getMobGoals().getGoal(m, KEY) instanceof AnimalEnhancedGoal g)) continue;
            if(!killer.equals(g.getTarget())) continue;
            if(g.testPersistentAnger(killer)) continue;
            g.setTarget(null);
            g.panic(DynamicLocation.createEntity(killer));
        }
    }
    protected long lastHit;
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void entityDamageMob(EntityDamageByEntityEvent event){
        if(!event.getEntity().equals(mob)) return;
        if(!(event.getDamager() instanceof LivingEntity dmger)) return;

        if((mob.getHealth() - event.getFinalDamage()) <= 0D){
            onDeath(dmger);
            //return;
        }

        if(CruxMath.hasOccurredWithin(lastHit, 100)) return;
        lastHit = System.currentTimeMillis();

        boolean aggressive = testAggression(dmger);
        for(Mob m : getNearbyAnimals()){
            if(!(Bukkit.getMobGoals().getGoal(m, KEY) instanceof AnimalEnhancedGoal g)) continue;
            if(aggressive){
                if(g.testAggression(dmger)){
                    g.setTarget(dmger);
                    continue;
                }
            }
            g.panic(DynamicLocation.createEntity(dmger));
        }
    }
}
