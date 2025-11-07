package killercreepr.usurvive.core.guide.particle;

import killercreepr.crux.core.Crux;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TrailRenderer {

    public static void renderTrail(Player player, List<Location> path) {
        if (path.isEmpty()) return;

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index >= path.size() || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location loc = path.get(index);
                player.spawnParticle(Particle.FLAME, loc, 1, 0, 0, 0, 0);
                index++;
            }
        }.runTaskTimer(Crux.getMainPlugin(), 0L, 2L); // move along trail
    }
}
