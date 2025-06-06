package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.ActiveEntityPotion;
import killercreepr.usurvive.core.enchant.logic.JoyWhirl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DisorientationPotion extends ActiveEntityPotion<LivingEntity> {
    public DisorientationPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(LivingEntity.class, potion, entity, duration, amplifier);
    }
    protected int period;

    @Override
    protected void onStart() {
        super.onStart();
        updatePeriod();
    }

    @Override
    protected void onUpdate(@NotNull LivingEntity e, int oldDuration, int oldAmplifier) {
        super.onUpdate(e, oldDuration, oldAmplifier);
        updatePeriod();
    }

    public void updatePeriod(){
        period = Math.max(60 - (amplifier * 15), 1);
    }

    protected int tick = 0;
    @Override
    protected void onTick() {
        super.onTick();
        if(tick % period == 0){
            rotate();
        }
        tick++;
    }

    public void rotate(){
        float speed = (amplifier + 1) * 15f;
        float pitchSpeed = (amplifier+1) * 15f;

        float yaw = CruxMath.random(-speed, speed);
        float pitch = CruxMath.random(-pitchSpeed, pitchSpeed);

        if(entity instanceof Player p){
            JoyWhirl.setRotation(p, yaw, pitch);
        }else{
            entity.setRotation(entity.getYaw() + (yaw), entity.getPitch() + pitch);
        }
    }
}
