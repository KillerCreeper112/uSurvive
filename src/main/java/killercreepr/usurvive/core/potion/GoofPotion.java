package killercreepr.usurvive.core.potion;

import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.ActiveEntityPotion;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class GoofPotion extends ActiveEntityPotion<LivingEntity> {
    protected final double oldEntitySize;
    public GoofPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(LivingEntity.class, potion, entity, duration, amplifier);
        updateMinAndMaxValues();
        if(parsedEntity == null) oldEntitySize = 0D;
        else{
            AttributeInstance attribute = parsedEntity.getAttribute(Attribute.SCALE);
            if(attribute == null) oldEntitySize = 0D;
            else oldEntitySize = attribute.getBaseValue();
        }
    }

    @Override
    protected void onStop(@NotNull LivingEntity e) {
        AttributeInstance attribute = e.getAttribute(Attribute.SCALE);
        if(attribute == null) return;
        attribute.setBaseValue(oldEntitySize);
    }

    @Override
    protected void onUpdate(@NotNull LivingEntity e, int oldDuration, int oldAmplifier) {
        updateMinAndMaxValues();
    }

    public void updateMinAndMaxValues(){
        minValue = -.5D - ((amplifier) * .3);
        maxValue = .5D + ((amplifier)*.3);
    }

    protected int tick = 0;
    protected double period = 20D; // Adjust this value to control the speed of oscillation
    protected double minValue;
    protected double maxValue;

    @Override
    protected void onTick(@NotNull LivingEntity e) {
        AttributeInstance attribute = e.getAttribute(Attribute.SCALE);
        if(attribute == null) return;

        double size = calculateSize(tick);

        attribute.setBaseValue(oldEntitySize + (1D+size));

        tick++;
    }

    public double calculateSize(int tick){
        double amplitude = (maxValue - minValue) / 2.0;
        return minValue + amplitude + amplitude * Math.sin(2 * Math.PI * (double) tick / period);
    }
}
