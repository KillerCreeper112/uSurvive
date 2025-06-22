package killercreepr.usurvive.core.entity.memory;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ParticleTrailHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("entity/particle/entity_effect");
    protected final Color color;
    public ParticleTrailHolder(@NotNull Key key, @NotNull EntityMemory parent, Color color) {
        super(key, parent);
        this.color = color;
    }

    public ParticleTrailHolder(@NotNull EntityMemory parent, Color color) {
        this(KEY, parent, color);
    }

    @Override
    public void tick(@NotNull Entity entity) {
        new ParticleBuilder(Particle.ENTITY_EFFECT)
            .color(color)
            .location(entity.getLocation())
            .spawn()
        ;
    }
}
