package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.ActiveEntityPotion;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class MendingBloomPotion extends ActiveEntityPotion<LivingEntity> {
    public MendingBloomPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(LivingEntity.class, potion, entity, duration, amplifier);
    }

    @Override
    protected void onUpdate(int oldDuration, int oldAmplifier) {
        super.onUpdate(oldDuration, oldAmplifier);
        healRate = calculateHealRate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        healRate = calculateHealRate();
    }

    public int calculateHealRate(){
        return Math.max(1, 200 - (amplifier*20));
    }

    public double calculateHeal(){
        return 1D;
    }

    protected int healRate = 1;

    @Override
    protected void onTick(@NotNull LivingEntity e) {
        super.onTick(e);

        if(Crux.getCurrentTick() % healRate != 0) return;

        heal(e);
    }

    public void heal(LivingEntity e){
        double hp = e.getHealth();
        double max = e.getAttribute(Attribute.MAX_HEALTH).getValue();
        if(hp >= max) return;
        e.setHealth(CruxMath.clamp(hp + calculateHeal(), 0D, max));
    }
}
