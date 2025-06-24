package killercreepr.usurvive.core.potion;

import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.SimpleActivePotion;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class PackAlliancePotion extends SimpleActivePotion implements Listener {
    public PackAlliancePotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(potion, entity, duration, amplifier);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if(!event.getDamager().equals(entity)) return;
        event.setDamage(event.getDamage()*calculateMultiplier());
    }

    public double calculateMultiplier(){
        return 1D + (0.1D + (amplifier * 0.1D));
    }

}
