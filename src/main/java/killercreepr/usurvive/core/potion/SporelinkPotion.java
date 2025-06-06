package killercreepr.usurvive.core.potion;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.entity.memory.SimplePotionHolder;
import killercreepr.cruxpotions.core.potions.ActiveEntityPotion;
import killercreepr.usurvive.core.lang.Lang;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SporelinkPotion extends ActiveEntityPotion<LivingEntity> {
    public SporelinkPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(LivingEntity.class, potion, entity, duration, amplifier);
    }

    protected int cooldown = 0;
    protected long lastMsg;

    public boolean attemptMsg(){
        if(CruxMath.hasOccurredWithin(lastMsg, 600)) return false;
        lastMsg = System.currentTimeMillis();
        Lang.SPORELINK_DAMAGED.use(entity);
        return true;
    }

    public boolean isOnCooldown(){
        return cooldown > 0;
    }

    public Collection<Entity> getSporeLinked(){
        return new GetEntityNear<>(Entity.class)
            .center(entity)
            .range(getEffectiveRadius())
            .filter(check ->{
                if(check.equals(entity)) return false;
                PotionHolder holder = EntityMemory.getDataHolder(check, SimplePotionHolder.class);
                if(holder == null) return false;
                if(holder.getPotion(potion) instanceof SporelinkPotion pot){
                    return !pot.isOnCooldown();
                }
                return false;
            })
            .find();
    }

    public void onSporeLinkDamage(Entity fromEntity){
        setCooldown(getEffectiveCooldown());
        attemptMsg();
    }

    public void onTakeDamage(EntityDamageEvent event){
        if(isOnCooldown()) return;
        var source = event.getDamageSource();
        getSporeLinked().forEach(hit ->{
            EntityDamager.entityDamager(hit, source.getDirectEntity())
                .setSource(source)
                .setCause(CruxEntityDamageEvent.DamageCause.fromBukkit(event.getCause()))
                .attack(event.getDamage());
            PotionHolder holder = EntityMemory.getDataHolder(hit, SimplePotionHolder.class);
            if(holder == null) return;
            if(holder.getPotion(potion) instanceof SporelinkPotion pot) pot.onSporeLinkDamage(event.getEntity());
        });
    }

    public void onTakeDamage(CruxEntityDamageEvent event){
        if(isOnCooldown()) return;
        var source = event.getSource();
        getSporeLinked().forEach(hit ->{
            EntityDamager.entityDamager(hit, source.getDirectEntity())
                .setSource(source)
                .setHitPosition(event.getHitPosition())
                .setCause(event.getCause())
                .attack(event.getTrueDmg(), event.getTrueKb(), event.getTrueUpKb());
            PotionHolder holder = EntityMemory.getDataHolder(hit, SimplePotionHolder.class);
            if(holder == null) return;
            if(holder.getPotion(potion) instanceof SporelinkPotion pot) pot.onSporeLinkDamage(event.getEntity());
        });
    }

    public long getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(long lastMsg) {
        this.lastMsg = lastMsg;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    protected void onTick() {
        super.onTick();
        if(cooldown > 0) cooldown--;
    }

    public double getEffectiveRadius(){
        return 12D * ((amplifier)*6D);
    }

    public int getEffectiveCooldown(){
        return Math.min(0, 80 - (amplifier * 10));
    }
}
