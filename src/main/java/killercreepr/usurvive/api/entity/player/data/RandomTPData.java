package killercreepr.usurvive.api.entity.player.data;

import killercreepr.crux.api.math.CruxLocation;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface RandomTPData {
    @Nullable Pos getLastTeleported(@NotNull Key world);
    void setLastTeleported(@NotNull Key world, @Nullable Pos location);
    boolean hasLastTeleported(@NotNull Key world);
    Map<Key, Pos> asMap();
    void setAllLastTeleported(Map<Key, Pos> map);

    interface Pos{
        @NotNull CruxLocation getLocation();
        long getTime();

        class Simple implements Pos{
            protected final CruxLocation location;
            protected final long time;

            public Simple(CruxLocation location, long time) {
                this.location = location;
                this.time = time;
            }

            @NotNull
            public CruxLocation getLocation() {
                return location;
            }

            public long getTime() {
                return time;
            }
        }
    }
}
