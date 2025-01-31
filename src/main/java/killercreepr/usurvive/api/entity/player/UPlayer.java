package killercreepr.usurvive.api.entity.player;

import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.data.PluginLoadable;
import killercreepr.crux.api.math.CruxLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface UPlayer extends Loadable, PluginLoadable {
    @NotNull UUID getUUID();
    long getLastLogout();
    @Nullable CruxLocation getLastKnownPosition();
    @Nullable String getLastKnownWorld();
    @Nullable String getLastKnownName();
    @NotNull Collection<UUID> getFriends();
    @NotNull Collection<UUID> getPartyMembers();

    void setLastLogout(long time);
    void setLastKnownPosition(@Nullable CruxLocation pos);
    void setLastKnownWorld(@Nullable String name);
    void setLastKnownName(@Nullable String name);

    boolean hasFriend(@NotNull UUID uuid);
    boolean isApartOfParty(@NotNull UUID uuid);
}
