package killercreepr.usurvive.core.potion;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import killercreepr.crux.core.Crux;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxattributes.core.component.CruxAttributeCompParsers;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.SimpleActivePotion;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.jetbrains.annotations.NotNull;

public class PiercingPotion extends SimpleActivePotion implements Listener {
    public PiercingPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(potion, entity, duration, amplifier);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerLaunchProjectile(PlayerLaunchProjectileEvent event) {
        if(!event.getPlayer().equals(entity)) return;
        CruxAttribute.addModifier(
            event.getProjectile(),
            CruxAttribute.ATTACK_PIERCE,
            CruxAttributeModifier.modifier(Crux.key("potion/piercing"), calculatePiercing())
        );
        CruxAttribute.addModifier(
            event.getProjectile(),
            CruxAttribute.ARMOR_PENETRATION,
            CruxAttributeModifier.modifier(Crux.key("potion/piercing"), calculateArmorPenetration())
        );
    }

    public int calculatePiercing(){
        return amplifier+1;
    }
    public double calculateArmorPenetration(){
        return 10 + (amplifier * 10);
    }
}
