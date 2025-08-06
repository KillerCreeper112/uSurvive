package killercreepr.usurvive.core.entity.mob.goals;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.usurvive.core.potion.USurvivePotions;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SnareEnchantGoal extends CruxMobModeledGoal implements Listener {
    public SnareEnchantGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_CREAKING_ACTIVATE, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,2f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_CREAKING_DEACTIVATE, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,1.7f);
            }
        });
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        super.onEntityDeath(event);
        if(!event.getEntity().equals(this.mob)) return;
        event.setDroppedExp(0);
        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        playAnimation("hurt", false);
    }

    public LivingEntity findTarget(){
        for(var e : mob.getWorld().getNearbyEntities(mob.getBoundingBox(),
            e -> e instanceof LivingEntity living && isValidTarget(living))){
            return (LivingEntity) e;
        }
        return null;
    }

    public int getLevel(){
        return CruxTag.get(mob, "level", PersistentDataType.INTEGER, 0);
    }

    protected int noTargetTimes = 0;
    @Override
    public void tick() {
        if(target == null){
            if(findTargetCooldown > 0){
                findTargetCooldown--;
                return;
            }
            findTargetCooldown = 8;
            LivingEntity found = findTarget();
            if(found == null){
                noTargetTimes++;
                if(noTargetTimes > 1){
                    mob.damage(9999999D);
                }
                return;
            }
            noTargetTimes = 0;
            setTarget(found);
            return;
        }
        if(!isValidTarget(target)){
            setTarget(null);
            return;
        }

        if(!mob.getBoundingBox().overlaps(target.getBoundingBox())){
            setTarget(null);
            return;
        }

        PotionHolder holder = PotionHolder.potionHolderOrCreate(target);
        if(holder == null){
            setTarget(null);
            return;
        }
        holder.addPotion(USurvivePotions.ENCUMBERED.create(target, 6, 4 + getLevel()));
    }
}
