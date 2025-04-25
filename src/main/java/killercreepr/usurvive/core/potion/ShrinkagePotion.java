package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.ActiveEntityPotion;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ShrinkagePotion extends ActiveEntityPotion<LivingEntity> {
    protected final Attribute bukkit = Attribute.SCALE;
    protected final NamespacedKey key = Crux.key("potion/shrinkage");
    public ShrinkagePotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(LivingEntity.class, potion, entity, duration, amplifier);
    }

    @Override
    protected void onUpdate(@NotNull LivingEntity e, int oldDuration, int oldAmplifier) {
        super.onUpdate(e, oldDuration, oldAmplifier);
        updateAttribute(e);
    }

    public void updateAttribute(LivingEntity e){
        var instance = e.getAttribute(bukkit);
        if(instance == null) return;

        double amount = ((amplifier+1) * -0.175);
        instance.removeModifier(key);
        instance.addTransientModifier(new AttributeModifier(key, amount, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
    }

    @Override
    protected void onStop(@NotNull LivingEntity e) {
        super.onStop(e);
        var instance = e.getAttribute(bukkit);
        if(instance == null) return;
        instance.removeModifier(key);
    }

    @Override
    protected void onStart(@NotNull LivingEntity e) {
        super.onStart(e);
        updateAttribute(e);
    }
}
