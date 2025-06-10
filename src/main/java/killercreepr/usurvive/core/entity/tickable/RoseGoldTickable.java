package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.core.Crux;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxtickables.api.entity.tickable.ActiveEntityTickable;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.SimpleActiveEntityTickable;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class RoseGoldTickable extends SimpleActiveEntityTickable {
    protected final NamespacedKey key = Crux.key("item/set_bonus/rose_gold");

    protected final float speedMultiplier;
    protected final float safeFallDistance;
    public RoseGoldTickable(Entity entity, EntityTickable tickable, CruxSlot slot, float speedMultiplier, float safeFallDistance) {
        super(entity, tickable, slot);
        this.speedMultiplier = speedMultiplier;
        this.safeFallDistance = safeFallDistance;
    }

    @Override
    public void started() {
        super.started();
        if(!(entity instanceof LivingEntity li)) return;
        var instance = li.getAttribute(Attribute.MOVEMENT_SPEED);
        if(instance != null){
            instance.addTransientModifier(new AttributeModifier(
                key, speedMultiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1
            ));
        }
        instance = li.getAttribute(Attribute.SAFE_FALL_DISTANCE);
        if(instance != null){
            instance.addTransientModifier(new AttributeModifier(
                key, safeFallDistance, AttributeModifier.Operation.ADD_NUMBER
            ));
        }
    }

    @Override
    public void stopped() {
        super.stopped();
        if(!(entity instanceof LivingEntity li)) return;
        var instance = li.getAttribute(Attribute.MOVEMENT_SPEED);
        if(instance != null){
            instance.removeModifier(key);
        }
        instance = li.getAttribute(Attribute.SAFE_FALL_DISTANCE);
        if(instance != null){
            instance.removeModifier(key);
        }
    }

    @Override
    public void tick() {

    }
}
