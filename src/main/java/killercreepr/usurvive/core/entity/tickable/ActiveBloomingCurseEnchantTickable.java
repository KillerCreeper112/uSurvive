package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.core.Crux;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ActiveBloomingCurseEnchantTickable extends ListenerActiveEntityTickable implements Listener {
    protected final NamespacedKey key = Crux.key("enchant/blooming_curse");
    protected int level;
    public ActiveBloomingCurseEnchantTickable(Entity entity, EntityTickable tickable, CruxSlot slot, int level) {
        super(entity, tickable, slot);
        this.level = level;
    }
    protected int fellBelowTimes = 0;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(entity)) return;

        if(!(entity instanceof LivingEntity living)) return;
        double max = living.getAttribute(Attribute.MAX_HEALTH).getValue();
        double hp = living.getHealth();
        double hpAfterDmg = hp - event.getFinalDamage();

        double percentNow = hp / max;
        double percentAfterDmg = hpAfterDmg / max;

        if(percentNow < .5D) return;
        if(percentAfterDmg >= .5D) return;

        onFellBelow50(living);
    }

    public boolean isBelow50(LivingEntity e){
        double hp = e.getHealth();
        double max = e.getAttribute(Attribute.MAX_HEALTH).getValue();
        double percentNow = hp / max;
        return percentNow < .5D;
    }

    public void onFellBelow50(LivingEntity e){
        fellBelowTimes++;

        e.getAttribute(Attribute.MAX_HEALTH).removeModifier(key);
        e.getAttribute(Attribute.MAX_HEALTH).addTransientModifier(
            new AttributeModifier(key, -fellBelowTimes, AttributeModifier.Operation.ADD_NUMBER)
        );
    }

    @Override
    public void stopped() {
        super.stopped();
        if(!(entity instanceof LivingEntity living)) return;
        living.getAttribute(Attribute.MAX_HEALTH).removeModifier(key);
    }

    @Override
    public void tick() {
        if(Crux.getCurrentTick() % 10 != 0) return;

        if(!(entity instanceof LivingEntity e)) return;
        if(isBelow50(e)){
            Crux.scheduler().runTask(() ->{
                e.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, level-1));
                e.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20, Math.max(level-2, 0)));
            });
        }
    }
}
