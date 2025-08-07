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

public class StandardParticleTrailHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("entity/standard_particle_trail");
    protected final ParticleBuilder particle;
    protected final int tickRate;
    public StandardParticleTrailHolder(@NotNull Key key, @NotNull EntityMemory parent, ParticleBuilder particle, int tickRate) {
        super(key, parent);
        this.particle = particle;
        this.tickRate = tickRate;
    }

    public StandardParticleTrailHolder(@NotNull EntityMemory parent, ParticleBuilder particle, int tickRate) {
        this(KEY, parent,particle, tickRate);
    }

    @Override
    public void tick(@NotNull Entity entity) {
        if(tickRate < 2 || Crux.getCurrentTick() % tickRate == 0){
            particle.location(entity.getLocation()).spawn();
        }
    }
}
