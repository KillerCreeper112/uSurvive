package killercreepr.usurvive.api.entity.sleeper;

import org.bukkit.event.entity.EntityDamageEvent;

public interface SlumberTargetLogic {
    void onTargetEntityTakeDamage(EntityDamageEvent event);
}
