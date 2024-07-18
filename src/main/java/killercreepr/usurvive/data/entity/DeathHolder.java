package killercreepr.usurvive.data.entity;

import killercreepr.crux.Crux;
import killercreepr.crux.data.entity.PlayerDataHolder;
import killercreepr.crux.data.entity.PlayerMemory;
import killercreepr.crux.data.world.CruxPosition;
import killercreepr.crux.util.CruxLoc;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.usurvive.death.DeathManager;
import killercreepr.usurvive.death.PlayerDeath;
import killercreepr.usurvive.values.ValuesProvider;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class DeathHolder extends PlayerDataHolder {
    public static final Key KEY = Crux.key("death");
    protected final @NotNull ValuesProvider cfg;
    protected final @NotNull DeathManager deathManager;
    public DeathHolder(@NotNull Key key, @NotNull PlayerMemory parent, @NotNull ValuesProvider cfg, @NotNull DeathManager deathManager) {
        super(key, parent);
        this.cfg = cfg;
        this.deathManager = deathManager;
    }

    public DeathHolder(@NotNull PlayerMemory parent, @NotNull ValuesProvider cfg, @NotNull DeathManager deathManager) {
        this(KEY, parent, cfg, deathManager);
    }

    protected int tick = 0;
    @Override
    public void tick(@NotNull Player p) {
        tick++;
        int every = cfg.DEATH_GUIDE_TICK_EVERY().value().intValue();
        if(every != 0 && tick % every != 0) return;
        tick = 0;
        tickDeathGuide(p);
    }

    public void tickDeathGuide(@NotNull Player p){
        Collection<PlayerDeath> deaths = deathManager.getAllDeathsInWorld(p);
        if(deaths.isEmpty()) return;

        for(PlayerDeath death : deaths){
            tickDeathGuide(p, death);
        }
    }

    public void tickDeathGuide(@NotNull Player p, @NotNull PlayerDeath death){
        Location pos = death.getPosition().toLocation(p.getWorld());

        Location startingPoint = CruxLoc.shiftToward(p.getEyeLocation(), pos, 1D);

    }
}
