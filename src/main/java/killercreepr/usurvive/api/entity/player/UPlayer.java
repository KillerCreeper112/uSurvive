package killercreepr.usurvive.api.entity.player;

import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.data.PluginLoadable;
import killercreepr.crux.api.math.CruxLocation;
import killercreepr.usurvive.core.USurvivePlugin;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface UPlayer extends Loadable, PluginLoadable {
    static UPlayer getPlayer(Entity e){
        if(!(e instanceof HumanEntity h)) return null;
        return USurvivePlugin.inst().getPlayerManager().getPlayer(h);
    }
    static UPlayer getPlayer(UUID uuid){
        return USurvivePlugin.inst().getPlayerManager().getPlayer(uuid);
    }
    @NotNull UUID getUUID();
    long getLastLogout();
    @Nullable CruxLocation getLastKnownPosition();
    @Nullable Key getLastKnownWorld();
    @Nullable String getLastKnownName();
    @NotNull Collection<UUID> getFriends();
    @NotNull Collection<UUID> getPartyMembers();

    void setLastLogout(long time);
    void setLastKnownPosition(@Nullable CruxLocation pos);
    void setLastKnownWorld(@Nullable Key key);
    void setLastKnownName(@Nullable String name);

    boolean hasFriend(@NotNull UUID uuid);
    boolean isApartOfParty(@NotNull UUID uuid);
    boolean isOnline();

    void load();
}
