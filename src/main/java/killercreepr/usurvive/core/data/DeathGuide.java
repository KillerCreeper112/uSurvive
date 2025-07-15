package killercreepr.usurvive.core.data;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.game.SimpleStatutable;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.usurvive.api.values.ValuesProvider;
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
    protected final @NotNull NumberProvider particleShiftStart;
    protected final @NotNull NumberProvider particleSpeedDistanceMultiplier;
    protected final @NotNull NumberProvider particleOffsetVertical;
    protected final @NotNull NumberProvider particleOffsetSide;
    protected final @NotNull NumberProvider particleShift;
    protected final Location toCloneSameY;
    protected final Particle particle;


    protected int currentParticleAmount;
    protected Location currentLocation;
    protected Vector dir;

    public DeathGuide(@NotNull Player p, @NotNull Location from, @NotNull Location location, int particleAmount, @NotNull NumberProvider particleShiftStart, @NotNull NumberProvider particleSpeedDistanceMultiplier, @NotNull NumberProvider particleOffsetVertical, @NotNull NumberProvider particleOffsetSide, @NotNull NumberProvider particleShift, Particle particle) {
        this.p = p;
        this.from = from;
        to = location;
        this.particleAmount = particleAmount;
        this.particleShiftStart = particleShiftStart;
        this.particleSpeedDistanceMultiplier = particleSpeedDistanceMultiplier;
        this.particleOffsetVertical = particleOffsetVertical;
        this.particleOffsetSide = particleOffsetSide;
        this.particleShift = particleShift;

        this.particle = particle;
        this.toCloneSameY = to.clone();
        this.toCloneSameY.setY(from.getY());
    }

    @Override
    public void started() {
        super.started();
        currentParticleAmount = particleAmount;
        currentLocation = CruxLoc.shiftToward(from.clone(), toCloneSameY, particleShiftStart.value().doubleValue());
        dir = currentLocation.getDirection();
        distance = p.getLocation().distance(to);
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

    public double getParticleY(){
        if(p.getY() >= to.getY()){
            return CruxMath.random(-.3D, -.2D);
        }
        return CruxMath.random(.2D, .3D);
    }

    protected double distance;
    @Override
    public void tick() {
        double speed = CruxMath.clamp(
            distance*particleSpeedDistanceMultiplier.value().doubleValue(),
            .01, .6
        );
        Location spawn = CruxLoc.shift(currentLocation.clone(), 0D,
            particleOffsetVertical.value().doubleValue(),
            particleOffsetSide.value().doubleValue());
        p.spawnParticle(particle, spawn, 0,
            dir.getX(), getParticleY(), dir.getZ(), speed
        );

        currentParticleAmount--;
        if(currentParticleAmount < 1){
            setStopped();
            return;
        }

        currentLocation = CruxLoc.shiftToward(currentLocation, toCloneSameY, particleShift.value().doubleValue());
    }
}
