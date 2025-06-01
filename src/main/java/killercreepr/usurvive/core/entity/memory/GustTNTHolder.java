package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class GustTNTHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("gust_tnt");
    public GustTNTHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }
    public GustTNTHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    protected Vector lastPosition;

    @Override
    public void tick(@NotNull Entity e) {
        Vector currentPosition = e.getLocation().toVector();

        if (lastPosition != null) {
            Vector direction = currentPosition.clone().subtract(lastPosition);
            if (direction.lengthSquared() > 0.001) {
                // Early exit if velocity is too low to bounce
                if (e.getVelocity().lengthSquared() < 0.1) return; // adjust threshold as needed

                Location from = lastPosition.toLocation(e.getWorld());

                // Ray trace between last and current position
                double distance = from.distance(e.getLocation()) + 0.1;
                RayTraceResult result = e.getWorld().rayTraceBlocks(e.getLocation(), direction.normalize(), distance, FluidCollisionMode.NEVER, true);
                if (result != null && result.getHitBlock() != null) {
                    Vector normal = result.getHitBlockFace().getDirection().multiply(-1);

                    Vector velocity = e.getVelocity();
                    Vector reflected = velocity.subtract(normal.multiply(2 * velocity.dot(normal))).multiply(0.8); // dampen
                    e.setVelocity(reflected);

                    Location impact = result.getHitPosition().toLocation(e.getWorld());
                    e.getWorld().spawnParticle(Particle.GUST, impact, 1, 0.1, 0.1, 0.1);
                }
            }
        }

        lastPosition = currentPosition.clone();
    }



}
