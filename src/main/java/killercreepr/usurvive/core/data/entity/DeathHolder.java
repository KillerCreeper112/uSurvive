package killercreepr.usurvive.core.data.entity;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.usurvive.api.death.DeathManager;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.data.DeathGuide;
import killercreepr.usurvive.core.death.PlayerDeath;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        return USurvivePlugin.inst().values().DEATH_EXPIRY_TIME().value().intValue();
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
        new DeathGuide(p, p.getLocation().add(0, p.getHeight()/2, 0), pos, cfg.DEATH_GUIDE_PARTICLE_AMOUNT().value().intValue(), cfg).setStarted();
    }
}
