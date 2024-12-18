package killercreepr.usurvive.api.world.module;

import killercreepr.cruxworlds.api.world.module.WorldModule;
import killercreepr.usurvive.core.death.PlayerDeath;
import killercreepr.usurvive.core.death.storage.WorldDeathStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

public interface DeathWorldModule extends WorldModule {
    @NotNull
    Collection<PlayerDeath> getDeaths(@Nullable Predicate<PlayerDeath> filter);
    @NotNull Collection<PlayerDeath> getPlayerDeaths(@NotNull UUID player);
    boolean addDeath(@NotNull PlayerDeath death);
    boolean removeDeath(@NotNull PlayerDeath death);
    @NotNull
    WorldDeathStorage getDeathStorage();
}
