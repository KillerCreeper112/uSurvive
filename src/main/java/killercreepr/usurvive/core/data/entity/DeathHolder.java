package killercreepr.usurvive.core.data.entity;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.cruxstatistics.api.bukkit.BukkitStatisticHolder;
import killercreepr.cruxstats.api.bukkit.BukkitStatHolder;
import killercreepr.usurvive.api.death.DeathManager;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.data.DeathGuide;
import killercreepr.usurvive.core.death.PlayerDeath;
import killercreepr.usurvive.core.stat.USurviveStats;
import killercreepr.usurvive.core.statistic.USurviveStatistics;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;

public class DeathHolder extends PlayerTickedDataHolder {
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

    public int getDeathExpiryTime(){
        Entity e = getParent().value();
        return (int) (e instanceof Player p ? BukkitStatHolder.holder(p)
            .getStatValue(USurviveStats.GRAVESTONE_EXPIRY_TICKS) :
            BukkitStatHolder.holder(parent.getUUID())
            .getStatValue(USurviveStats.GRAVESTONE_EXPIRY_TICKS));
    }

    public void tabTick(Player p){
        int every = cfg.UPDATE_TAB_TIME().value().intValue();
        if(every == 0 || (tick % every != 0)) return;
        MergedTagContainer tags = TagContainer.merged().hook(p)
            .hook(Crux.getServer());
        p.sendPlayerListHeaderAndFooter(
            Crux.format().deserialize(cfg.TAB_HEADER().valueOr(""), tags),
            Crux.format().deserialize(cfg.TAB_FOOTER().valueOr(""), tags)
        );
        cfg.PLAYER_TAB_NAME().ifPresent(name ->{
            p.playerListName(Crux.format().deserialize(name, tags));
        });

        if (p.getIdleDuration().compareTo(Duration.ofMinutes(3)) >= 0) {
            var holder = BukkitStatisticHolder.statisticHolder(p);
            if(holder != null) holder.incrementStatistic(USurviveStatistics.IDLE_TIME, 1);
        }
    }

    //merged tab updater here for optimization.
    protected int tick = 0;
    @Override
    public void onTick(@NotNull Player p) {
        tabTick(p);
        tick++;
        int every = cfg.DEATH_GUIDE_TICK_EVERY().value().intValue();
        if(every==0) return;
        if(tick % every != 0) return;
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
        //Entity guardian = death.getGuardian();
        Location pos =  death.getGravePosition().toLocation(p.getWorld()); //guardian == null ? death.getGravePosition().toLocation(p.getWorld()) : guardian.getLocation();

        //Location startingPoint = CruxLoc.shiftToward(p.getEyeLocation(), pos, 1D);
        new DeathGuide(p, p.getLocation().add(0, p.getHeight()/2, 0), pos,
            cfg.DEATH_GUIDE_PARTICLE_AMOUNT().value().intValue(),
            cfg.DEATH_GUIDE_PARTICLE_SHIFT_START(),
            cfg.DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER(),
            cfg.DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL(),
            cfg.DEATH_GUIDE_PARTICLE_OFFSET_SIDE(),
            cfg.DEATH_GUIDE_PARTICLE_SHIFT(),
            Particle.SOUL_FIRE_FLAME
        ).setStarted();
    }
}
