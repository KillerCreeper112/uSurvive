package killercreepr.usurvive.api.entity.player;

import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.data.PluginLoadable;
import killercreepr.crux.api.math.CruxLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface UPlayer extends Loadable, PluginLoadable {
    @NotNull UUID getUUID();
    long getLastLogout();
    @Nullable CruxLocation getLastKnownPosition();
    @Nullable String getLastKnownWorld();
    @Nullable String getLastKnownName();

    void setLastLogout(long time);
    void setLastKnownPosition(@Nullable CruxLocation pos);
    void setLastKnownWorld(@Nullable String name);
    void setLastKnownName(@Nullable String name);
}
