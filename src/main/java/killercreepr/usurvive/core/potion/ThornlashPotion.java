package killercreepr.usurvive.core.potion;

import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.api.potion.inflictor.PotionInflictor;
import killercreepr.cruxpotions.core.potions.InflictedPotion;
import killercreepr.cruxpotions.core.potions.inflictor.BlockInflictor;
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import killercreepr.usurvive.core.damage.type.USurviveDamageTypes;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThornlashPotion extends InflictedPotion {
    protected int damageRate = 1;
    protected final LivingEntity living;
    public ThornlashPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        this(potion, entity, duration, amplifier, null);
    }

    public ThornlashPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
        super(potion, entity, duration, amplifier, inflictor);
        if(entity instanceof LivingEntity l) living = l;
        else living = null;
    }

    @Override
    protected void onUpdate(int oldDuration, int oldAmplifier) {
        super.onUpdate(oldDuration, oldAmplifier);
        updateDamageRate();
    }

    public void updateDamageRate(){
        damageRate = Math.max(1, 60 - (amplifier*10));
    }

    @Override
    public InflictedPotion setInflictor(@Nullable PotionInflictor inflictor) {
        super.setInflictor(inflictor);
        damageSource = buildDamageSource();
        return this;
    }

    public DamageSource buildDamageSource(){
        if(inflictor == null) return null;
        var builder = DamageSource.builder(USurviveDamageTypes.THORNLASH);

        if(inflictor instanceof EntityInflictor entityInflictor){
            Entity got = entityInflictor.value();
            if(got != null) builder.withDirectEntity(got);
        }else if(inflictor instanceof BlockInflictor blockInflictor){
            /*Block got = blockInflictor.value();
            if(got != null) builder.withDamageLocation(got.getLocation().toCenterLocation());*/
        }

        return builder.build();
    }

    protected DamageSource damageSource;
    /*public boolean shouldUpdateDamageSource(){
        if(damageSource == null) return false;
        Entity e = damageSource.getCausingEntity();
        if(e != null && !CruxEntityUtil.isValid(e)) return true;
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        if(living == null){
            setDuration(0);
            return;
        }
        damageSource = buildDamageSource();
        updateDamageRate();
    }

    protected int tick = -1;
    @Override
    protected void onTick() {
        super.onTick();
        tick++;
        if(tick % damageRate != 0) return;
        Crux.scheduler().runTaskMain(() ->{
            if(!CruxEntityUtil.isValid(living)) return;
            EntityDamager.entityDamager(living)
                .setSource(damageSource)
                .setCause(CruxEntityDamageEvent.DamageCause.fromBukkit(EntityDamageEvent.DamageCause.THORNS))
                .attack(2D);
        });
    }
}
