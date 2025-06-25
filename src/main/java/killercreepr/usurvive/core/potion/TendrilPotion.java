package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.units.qual.K;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

//todo
public class TendrilPotion extends AttributesPotion implements Listener {
    public TendrilPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier, Map<Attribute, PotModCreator> attributes) {
        super(potion, entity, duration, amplifier, attributes);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!event.getDamager().equals(entity)) return;
        DamageSource source = event.getDamageSource();
        if(source != DamageType.MOB_ATTACK && source != DamageType.PLAYER_ATTACK){
            return;
        }

    }

    public double calculateLifeStealChance(){
        return (amplifier+1) * 30;
    }

    public double calculateLifeStealPercent(){
        return (amplifier+1) * .2;
    }
}
