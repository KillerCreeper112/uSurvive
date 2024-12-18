package killercreepr.usurvive.api.death;

import killercreepr.usurvive.api.world.module.DeathWorldModule;
import killercreepr.usurvive.core.death.PlayerDeath;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public interface DeathManager {
    @NotNull
    Collection<PlayerDeath> getAllDeaths(@Nullable Predicate<PlayerDeath> filter);
    @NotNull Collection<PlayerDeath> getAllDeathsInWorld(@NotNull UUID player, @NotNull UUID world);
    @NotNull Collection<PlayerDeath> getAllDeaths(@NotNull UUID player);
    default @NotNull Collection<PlayerDeath> getAllDeaths(@NotNull Player player){
        return getAllDeaths(player.getUniqueId());
    }
    default @NotNull Collection<PlayerDeath> getAllDeathsInWorld(@NotNull Player p){
        return getAllDeathsInWorld(p.getUniqueId(), p.getWorld().getUID());
    }
    boolean addDeath(@NotNull PlayerDeath death);
    boolean removeDeath(@NotNull PlayerDeath death);
    @NotNull
    Map<UUID, PlayerDeath> getEchoToPlayerDeath();
    @Nullable PlayerDeath getPlayerDeathByGuardian(@NotNull UUID echo);

    boolean addDeathWorldModule(DeathWorldModule module);
    boolean removeDeathWorldModule(DeathWorldModule module);
    Collection<DeathWorldModule> getHookedDeathWorldModules();
}
