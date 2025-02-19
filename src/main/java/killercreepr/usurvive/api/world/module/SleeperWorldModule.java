package killercreepr.usurvive.api.world.module;

import killercreepr.cruxworlds.api.world.module.WorldModule;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface SleeperWorldModule extends WorldModule {
    int getSleepersAmount();
    @NotNull Collection<UUID> getSleepers();
    void addSleeper(@NotNull UUID uuid);
    void removeSleeper(@NotNull UUID uuid);
    boolean hasSleeper(@NotNull UUID uuid);

    int getDeepSleepersAmount();
    @NotNull Collection<UUID> getDeepSleepers();
    void addDeepSleeper(@NotNull UUID uuid);
    void removeDeepSleeper(@NotNull UUID uuid);
    boolean hasDeepSleeper(@NotNull UUID uuid);

    int getTimeSkipPerTick();
}
