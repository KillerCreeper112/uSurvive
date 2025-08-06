package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.ActiveEntityPotion;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AttributesPotion extends ActiveEntityPotion<LivingEntity> {
    protected final Map<Attribute, PotModCreator> attributes;
    public AttributesPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier, Map<Attribute, PotModCreator> attributes) {
        super(LivingEntity.class, potion, entity, duration, amplifier);
        this.attributes = attributes;
    }

    @Override
    protected void onUpdate(@NotNull LivingEntity e, int oldDuration, int oldAmplifier) {
        super.onUpdate(e, oldDuration, oldAmplifier);
        if(oldAmplifier == amplifier) return;
        updateAttribute(e);
    }

    public void updateAttribute(LivingEntity e){
        attributes.forEach((att, creator) ->{
            var instance = e.getAttribute(att);
            if(instance == null) return;
            var key = Crux.key("potion/" + potion.key().value());
            var modifier = new AttributeModifier(key, creator.calculateAmount(e, amplifier),
                creator.getOperation());
            instance.removeModifier(key);
            instance.addTransientModifier(modifier);
        });
    }

    @Override
    protected void onStop(@NotNull LivingEntity e) {
        super.onStop(e);
        attributes.forEach((att, creator) ->{
            var instance = e.getAttribute(att);
            if(instance == null) return;
            var key = Crux.key("potion/" + potion.key().value());
            instance.removeModifier(key);
        });
    }

    @Override
    protected void onStart(@NotNull LivingEntity e) {
        super.onStart(e);
        updateAttribute(e);
    }

    public interface PotModCreator{
        double calculateAmount(LivingEntity e, int amplifier);
        default AttributeModifier.Operation getOperation(){
            return AttributeModifier.Operation.ADD_NUMBER;
        }
    }
}
