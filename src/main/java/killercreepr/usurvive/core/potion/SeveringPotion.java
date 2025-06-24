package killercreepr.usurvive.core.potion;

import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.SimpleActivePotion;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class SeveringPotion extends SimpleActivePotion implements Listener {
    public SeveringPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(potion, entity, duration, amplifier);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!event.getDamager().equals(entity)) return;
        if(CruxMob.isInCategory(event.getEntity(), MobCategory.UNDEAD)){
            event.setDamage(event.getDamage()*calculateMultiplier());
        }
    }

    public double calculateMultiplier(){
        return 1D + (0.2 * (amplifier+1));
    }
}
