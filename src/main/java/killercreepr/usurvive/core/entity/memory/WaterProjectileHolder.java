package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.usurvive.api.component.WaterProjectile;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class WaterProjectileHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("water_projectile");
    public WaterProjectileHolder(@NotNull Key key, @NotNull EntityMemory parent, WaterProjectile data) {
        super(key, parent);
        this.data = data;
    }

    public WaterProjectileHolder(@NotNull EntityMemory parent, WaterProjectile data) {
        this(KEY, parent, data);
    }

    protected final WaterProjectile data;

    protected int tick = 0;
    @Override
    public void tick(@NotNull Entity entity) {
        tick++;
        if(tick >= data.maxTime()) return;
        //double dropOffSpeed = 2.0; // Adjust this to control how fast the force weakens (Higher = faster drop-off)
        double maxTicks = data.maxTime() * (1.0 / data.dropOffRate()); // Scale the max ticks based on drop-off speed
        double decayFactor = Math.max(0, 1 - Math.pow(tick / maxTicks, 2));

        Vector dir = entity.getLocation().getDirection().normalize();
        dir.multiply(data.force() * decayFactor);
        entity.setVelocity(entity.getVelocity().add(dir));
    }
}
