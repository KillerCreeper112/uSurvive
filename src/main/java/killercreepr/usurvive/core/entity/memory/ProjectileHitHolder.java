package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityDataHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Does not work because it's removed before the event
 */
@Deprecated(forRemoval = true)
public class ProjectileHitHolder extends EntityDataHolder {
    public static final Key KEY = Crux.key("projectile_hit");
    public ProjectileHitHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }
    public ProjectileHitHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    public void onProjectileHit(ProjectileHitEvent event){

    }
}
