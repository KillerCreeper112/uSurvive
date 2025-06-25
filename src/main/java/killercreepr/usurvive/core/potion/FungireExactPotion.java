package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FungireExactPotion extends AttributesPotion{
    public FungireExactPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier, Map<Attribute, PotModCreator> attributes) {
        super(potion, entity, duration, amplifier, attributes);
        this.maxDuration = duration;
    }

    @Override
    protected void onUpdate(@NotNull LivingEntity e, int oldDuration, int oldAmplifier) {
        super.onUpdate(e, oldDuration, oldAmplifier);
        if(duration > maxDuration) maxDuration = duration;
        else{
            int dif = duration - oldDuration;
            if(dif > 0){
                maxDuration += dif;
            }
        }
    }

    protected int maxDuration;

    @Override
    protected void onStop(@NotNull LivingEntity e) {
        super.onStop(e);
        Crux.scheduler().runTaskMain(() ->{
            e.addPotionEffect(new PotionEffect(
                PotionEffectType.HUNGER, maxDuration, 5
            ));
        });
    }
}
