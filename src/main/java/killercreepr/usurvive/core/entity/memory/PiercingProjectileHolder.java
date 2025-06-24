package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityDataHolder;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PiercingProjectileHolder extends EntityDataHolder {
    public static final Key KEY = Crux.key("piercing_projectile");
    public PiercingProjectileHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }
    public PiercingProjectileHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }
    protected final Map<UUID, Long> lastHit = new HashMap<>();

    public Map<UUID, Long> getLastHit() {
        return lastHit;
    }
}
