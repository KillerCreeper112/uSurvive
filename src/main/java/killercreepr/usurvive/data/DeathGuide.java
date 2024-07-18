package killercreepr.usurvive.data;

import killercreepr.crux.Crux;
import killercreepr.crux.game.SimpleStatutable;
import killercreepr.crux.util.CruxLoc;
import killercreepr.crux.util.CruxMath;
import killercreepr.usurvive.values.ValuesProvider;
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
    protected final @NotNull ValuesProvider cfg;
    public DeathGuide(@NotNull Player p, @NotNull Location from, @NotNull Location to, int particleAmount, @NotNull ValuesProvider cfg) {
        this.p = p;
        this.from = from;
        this.to = to;
        this.particleAmount = particleAmount;
        this.cfg = cfg;
    }

    protected int currentParticleAmount;
    protected Location currentLocation;

    @Override
    public void started() {
        super.started();
        currentParticleAmount = particleAmount;
        currentLocation = CruxLoc.shiftToward(from.clone(), to, cfg.DEATH_GUIDE_PARTICLE_SHIFT_START().value().doubleValue());
        new BukkitRunnable(){
            @Override
            public void run() {
                if(shouldStop()){
                    cancel();
                    return;
                }
                tick();
            }
        }.runTaskTimer(Crux.getMainPlugin(), 0L, 3L);
    }

    @Override
    public void tick() {
        double distance = currentLocation.distance(to);
        Vector dir = currentLocation.getDirection().normalize();
        double speed = CruxMath.clamp(
            distance*cfg.DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER().value().doubleValue(),
            .5, 1.5
        );
        p.spawnParticle(Particle.SOUL_FIRE_FLAME, currentLocation, 0,
            dir.getX(), 0D, dir.getZ(), speed
        );

        currentParticleAmount--;
        if(currentParticleAmount < 1){
            setStopped();
            return;
        }
        CruxLoc.shiftToward(currentLocation, to, cfg.DEATH_GUIDE_PARTICLE_SHIFT().value().doubleValue());
    }
}
