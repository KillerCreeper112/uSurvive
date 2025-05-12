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

public class SpettleProjectileHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("mob/spettle/projectile");
    public SpettleProjectileHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }

    public SpettleProjectileHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    @Override
    public void tick(@NotNull Entity entity) {
        new ParticleBuilder(Particle.ENTITY_EFFECT)
            .color(Color.fromRGB(0x5B5EB0))
            .location(entity.getLocation())
            .spawn()
        ;
    }
}
