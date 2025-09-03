package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.util.CruxMath;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CruxEntityDamagerTesterHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("crux_entity_damager_tester");
    public CruxEntityDamagerTesterHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }
    public CruxEntityDamagerTesterHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    public final Map<UUID, Long> cruxDmged = new HashMap<>();

    public boolean cruxDmgedWithin(Entity e, int time){
        return CruxMath.hasOccurredWithin(cruxDmged.getOrDefault(e.getUniqueId(), 0L), time);
    }

    @Override
    public void tick(@NotNull Entity entity) {

    }
}
