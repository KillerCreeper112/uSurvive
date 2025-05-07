package killercreepr.usurvive.api.entity.player.data;

import killercreepr.crux.api.math.CruxLocation;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.usurvive.core.USurvivePlugin;
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
        long getWorldTime();
        long getTime();
        boolean hasExpired();

        class Simple implements Pos{
            protected final CruxLocation location;
            protected final long worldTime;
            protected final long time;

            public Simple(CruxLocation location, long worldTime, long time) {
                this.location = location;
                this.worldTime = worldTime;
                this.time = time;
            }

            @NotNull
            public CruxLocation getLocation() {
                return location;
            }

            public long getWorldTime() {
                return worldTime;
            }

            @Override
            public long getTime() {
                return time;
            }

            @Override
            public boolean hasExpired() {
                return !CruxMath.hasOccurredWithin(time, USurvivePlugin.inst().values().RESET_SET_RANDOM_TP_TIME().value().intValue());
            }
        }
    }
}
