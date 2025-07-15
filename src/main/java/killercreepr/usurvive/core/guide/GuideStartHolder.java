package killercreepr.usurvive.core.guide;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.data.DeathGuide;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GuideStartHolder extends PlayerTickedDataHolder {
    public static final Key KEY = Crux.key("guide/start");
    public GuideStartHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
    }
    public GuideStartHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    protected Location fallenMeteorLocation;

    public Location getFallenMeteorLocation() {
        return fallenMeteorLocation;
    }

    public void setFallenMeteorLocation(Location fallenMeteorLocation) {
        this.fallenMeteorLocation = fallenMeteorLocation;
    }

    public DataFile getSaveFile(boolean createIfNeeded){
        return BukkitDataFile.parseFromGeneralPath(
            new CruxFolder(USurvivePlugin.inst(), "data/guide/player/" + parent.getUUID()).file(),
            createIfNeeded
        );
    }

    public boolean deleteSaveFile(){
        DataFile file = getSaveFile(false);
        if(file == null) return false;
        return file.delete();
    }

    @Override
    protected void onTick(@NotNull Player p) {
        super.onTick(p);
        int every = 30;//cfg.DEATH_GUIDE_TICK_EVERY().value().intValue();
        if(every==0) return;
        if(Crux.getCurrentTick() % every != 0) return;
        tickDeathGuide(p);
    }

    public void tickDeathGuide(@NotNull Player p){
        if(fallenMeteorLocation == null) return;
        //Entity guardian = death.getGuardian();
        Location pos =  fallenMeteorLocation;

        var cfg = USurvivePlugin.inst().values();
        //Location startingPoint = CruxLoc.shiftToward(p.getEyeLocation(), pos, 1D);
        new DeathGuide(p, p.getLocation().add(0, p.getHeight()/2, 0), pos,
            cfg.DEATH_GUIDE_PARTICLE_AMOUNT().value().intValue(),
            cfg.DEATH_GUIDE_PARTICLE_SHIFT_START(),
            cfg.DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER(),
            cfg.DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL(),
            cfg.DEATH_GUIDE_PARTICLE_OFFSET_SIDE(),
            cfg.DEATH_GUIDE_PARTICLE_SHIFT(),
            Particle.FLAME
        ).setStarted();
    }
}
