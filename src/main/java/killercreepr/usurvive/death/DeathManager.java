package killercreepr.usurvive.death;

import killercreepr.crux.data.LocationPos;
import killercreepr.usurvive.death.storage.MultiVerseDeathStorage;
import killercreepr.usurvive.entity.USurviveMobs;
import killercreepr.usurvive.event.PlayerEchoDeathEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class DeathManager implements Listener {
    protected final MultiVerseDeathStorage deaths = new MultiVerseDeathStorage(
        new HashMap<>(), this
    );
    protected final Map<UUID, PlayerDeath> echoToPlayerDeath = new HashMap<>();
    public @NotNull Collection<PlayerDeath> getAllDeaths(@Nullable Predicate<PlayerDeath> filter){
        Collection<PlayerDeath> list = new HashSet<>();
        deaths.forEach(world ->{
            world.forEach(chunk ->{
                chunk.forEach(l ->{
                    if(filter != null && !filter.test(l)) return;
                    list.add(l);
                });
            });
        });
        return list;
    }

    public @NotNull Collection<PlayerDeath> getAllDeaths(@NotNull UUID player){
        return getAllDeaths(e -> e.getPlayerUUID().equals(player));
    }

    public @NotNull Collection<PlayerDeath> getAllDeaths(@NotNull Player player){
        return getAllDeaths(player.getUniqueId());
    }

    public MultiVerseDeathStorage getDeaths() {
        return deaths;
    }

    public Map<UUID, PlayerDeath> getEchoToPlayerDeath() {
        return echoToPlayerDeath;
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getPlayer();
        int totalExperience = p.calculateTotalExperiencePoints();

        if(event.getDrops().isEmpty() && totalExperience < 1){
            event.getDrops().clear();
            event.setDroppedExp(0);
            return;
        }

        Entity echo = USurviveMobs.ECHO.spawn(p.getLocation());

        PlayerDeath death = new PlayerDeath(
            p.getUniqueId(), p.getWorld().getUID(),
            LocationPos.from(p.getLocation()), echo.getUniqueId(),
            new ArrayList<>(event.getDrops()), totalExperience
        );

        PlayerEchoDeathEvent deathEvent = new PlayerEchoDeathEvent(this, death);
        if(!deathEvent.callEvent()){
            echo.remove();
            return;
        }
        PlayerDeath newDeath = deathEvent.getDeath();
        if(!newDeath.getGuardianUUID().equals(death.getGuardianUUID())) echo.remove();
        //todo maybe make something to take care of multiple deaths taken place at the same block location.
        deaths.add(newDeath.getWorldUUID(), p.getChunk().getChunkKey(), newDeath);
        event.getDrops().clear();
        event.setDroppedExp(0);
    }
}
