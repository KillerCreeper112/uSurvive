package killercreepr.usurvive.data;

import killercreepr.crux.Crux;
import killercreepr.crux.game.SimpleStatutable;
import killercreepr.crux.util.CruxLoc;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DeathGuide extends SimpleStatutable {
    protected final @NotNull Player p;
    protected final @NotNull Location from;
    protected final @NotNull Location to;
    protected final int particleAmount;
    public DeathGuide(@NotNull Player p, @NotNull Location from, @NotNull Location to, int particleAmount) {
        this.p = p;
        this.from = from;
        this.to = to;
        this.particleAmount = particleAmount;
    }

    protected int currentParticleAmount;
    protected Location currentLocation;

    @Override
    public void started() {
        super.started();
        currentParticleAmount = particleAmount;
        currentLocation = CruxLoc.shiftToward(from.clone(), to, 1D);
        new BukkitRunnable(){
            @Override
            public void run() {
                if(shouldStop()){
                    cancel();
                    return;
                }
                tick();
            }
        }.runTaskTimer(Crux.getMainPlugin(), 0L, 10L);
    }

    @Override
    public void tick() {
        Vector dir = currentLocation.getDirection();
        p.spawnParticle(Particle.SOUL_FIRE_FLAME, currentLocation, 0,
            dir.getX(), dir.getY(), dir.getZ(), .2D
        );

        currentParticleAmount--;
        if(currentParticleAmount < 1){
            setStopped();
            return;
        }
        CruxLoc.shiftToward(currentLocation, from, 1D);
    }
}
