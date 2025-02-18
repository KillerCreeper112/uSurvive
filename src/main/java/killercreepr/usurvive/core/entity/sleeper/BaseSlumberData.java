package killercreepr.usurvive.core.entity.sleeper;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.usurvive.api.entity.sleeper.SlumberData;
import killercreepr.usurvive.api.entity.sleeper.SlumberSpectator;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.lang.Lang;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

public class BaseSlumberData implements SlumberData, SlumberSpectator {
    protected final Entity entity;
    protected final Location location;
    protected Entity display;
    protected Entity spectator;
    protected GameMode previousGameMode;
    protected boolean forceStop = false;

    public BaseSlumberData(Entity entity, Location location) {
        this.entity = entity;
        this.location = location;
    }

    public Location getUserTeleportLocation(){
        return location;
    }

    public Location getDisplayLocation(){
        return location;
    }

    @Override
    public boolean shouldStop() {
        return forceStop;
    }

    @Override
    public void started() {
        if(!(entity instanceof Player p)){
            entity.setPose(Pose.SLEEPING, true);
            return;
        }

        spectator = USurviveMobs.SLEEPER_OBJECT.spawn(getUserTeleportLocation());
        display = USurviveMobs.SLEEPER_DISPLAY.spawn(getDisplayLocation());
        PlayerDisguise disguise = new PlayerDisguise(p);
        disguise.getWatcher().setSleeping(true);
        disguise.getWatcher().setNameVisible(false);
        DisguiseAPI.disguiseEntity(display, disguise);

        previousGameMode = p.getGameMode();
        p.setGameMode(GameMode.SPECTATOR);

        p.teleport(spectator);
        p.setSpectatorTarget(spectator);
    }

    @Override
    public void stopped() {
        if(!(entity instanceof Player p)){
            entity.setPose(Pose.SLEEPING, true);
        }else{
            p.setSpectatorTarget(null);
            if(previousGameMode != null) p.setGameMode(previousGameMode);
        }
        if(display != null) display.remove();
        if(spectator != null) spectator.remove();
    }

    @Override
    public void tick() {
        Lang.SLEEPING_TICK.use(entity, TagContainer.merged().hook(entity));
    }

    @Override
    public void onStopSpectating(PlayerStopSpectatingEntityEvent event) {
        event.setCancelled(true);
        forceStop = true;
    }
}
