package killercreepr.usurvive.core.potion;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TendrilGraspPotion extends AttributesPotion implements Listener {
    public TendrilGraspPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier, Map<Attribute, PotModCreator> attributes) {
        super(potion, entity, duration, amplifier, attributes);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!event.getDamager().equals(entity)) return;
        if(!(event.getEntity() instanceof LivingEntity victim)) return;
        DamageSource source = event.getDamageSource();
        if(source.getDamageType() != DamageType.MOB_ATTACK && source.getDamageType() != DamageType.PLAYER_ATTACK){
            return;
        }
        if(!CruxMath.testChance(calculateLifeStealChance())) return;

        double damage = event.getFinalDamage();
        double hp = victim.getHealth();

        double amount = Math.min(hp, damage);

        if(amount < 1D) return;

        double heal = amount * calculateLifeStealPercent();
        if(!(entity instanceof LivingEntity dmger)) return;
        dmger.setHealth(
            CruxMath.clamp(dmger.getHealth() + heal, 0D, dmger.getAttribute(Attribute.MAX_HEALTH).getValue())
        );
        new ParticleBuilder(Particle.HEART)
            .location(dmger.getLocation().add(0, dmger.getHeight()/2, 0))
            .speed(.1)
            .offset(.3, .3, .3)
            .count(CruxMath.random(2, 4))
            .spawn()
        ;
    }

    public double calculateLifeStealChance(){
        return (amplifier+1) * 30;
    }

    public double calculateLifeStealPercent(){
        return (amplifier+1) * .2;
    }
}
