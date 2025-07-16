package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.api.potion.inflictor.LocationedInflictor;
import killercreepr.cruxpotions.api.potion.inflictor.PotionInflictor;
import killercreepr.cruxpotions.core.potions.InflictedPotion;
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrostbitePotion extends InflictedPotion {
    public FrostbitePotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(potion, entity, duration, amplifier);
    }

    public FrostbitePotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
        super(potion, entity, duration, amplifier, inflictor);
    }

    public static int calculateDuration(int amplifier){
        return 140 + ((6 * (amplifier+1)) * 20);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(entity instanceof LivingEntity l) {
            Crux.scheduler().runTaskMain(() -> {
                l.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, (40 + ((amplifier) * 80)), (amplifier+1)));
            });
        }
        entity.setFreezeTicks(duration);
    }

    protected int tick = 0;
    @Override
    protected void onTick() {
        super.onTick();
        if(entity.getFreezeTicks() <= 140){
            duration = 0;
            return;
        }

        tick++;
        if(tick >= 30 - ((amplifier+1) * 10)){
            tick = 0;
            Crux.scheduler().runTaskMain(() ->{
                var builder = DamageSource.builder(DamageType.FREEZE);
                var dmger = EntityDamager.entityDamager(entity);

                if(inflictor instanceof EntityInflictor d){
                    Entity e = d.value();
                    if(e != null){
                        builder.withDirectEntity(e);
                        dmger.setDamager(e);
                    }
                }else if(inflictor instanceof LocationedInflictor d){
                    Location l = d.getLocation();
                    if(l != null) builder.withDamageLocation(l);
                }
                dmger.setSource(builder.build());
                dmger.attack(1D);
            });
        }
    }
}
