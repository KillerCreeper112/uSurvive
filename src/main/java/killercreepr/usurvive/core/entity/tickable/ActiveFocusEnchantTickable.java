package killercreepr.usurvive.core.entity.tickable;

import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.SimpleActiveEntityTickable;
import org.bukkit.entity.Entity;

public class ActiveFocusEnchantTickable extends SimpleActiveEntityTickable {
    public ActiveFocusEnchantTickable(Entity entity, EntityTickable tickable, CruxSlot slot) {
        super(entity, tickable, slot);
    }

    @Override
    public void tick() {

    }
}
