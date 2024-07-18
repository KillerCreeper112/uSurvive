package killercreepr.usurvive.death;

import com.google.common.base.Preconditions;
import killercreepr.crux.data.LocationPos;
import killercreepr.crux.data.world.WorldChunkStorage;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.usurvive.death.storage.ChunkDeathStorage;
import killercreepr.usurvive.death.storage.MultiVerseDeathStorage;
import killercreepr.usurvive.entity.USurviveMobs;
import killercreepr.usurvive.event.PlayerEchoDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
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

    public @NotNull Collection<PlayerDeath> getAllDeathsInWorld(@NotNull Player p){
        return getAllDeathsInWorld(p.getUniqueId(), p.getWorld().getUID());
    }

    public @NotNull Collection<PlayerDeath> getAllDeathsInWorld(@NotNull UUID player, @NotNull UUID world){
        WorldChunkStorage<PlayerDeath> storage = deaths.get(world);
        if(storage==null) return Set.of();
        Collection<PlayerDeath> list = new HashSet<>();
        storage.forEach(s ->{
            s.forEach(death ->{
                if(death.getPlayerUUID().equals(player)) list.add(death);
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

    public @Nullable PlayerDeath getPlayerDeathByGrave(@NotNull UUID echo){
        return echoToPlayerDeath.get(echo);
    }

    public Map<UUID, PlayerDeath> getEchoToPlayerDeath() {
        return echoToPlayerDeath;
    }

    public @NotNull PlayerDeath getOldestDeath(@NotNull Collection<PlayerDeath> list){
        Preconditions.checkArgument(!list.isEmpty(), "PlayerDeath collection must not be empty!");
        PlayerDeath oldest = null;
        for(PlayerDeath d : list){
            if(oldest == null || d.getTime() < oldest.getTime()) oldest = d;
        }
        return Objects.requireNonNull(oldest);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        //remove old player deaths that were removed while the echo mob was saved in chunks
        for(Entity e : event.getEntities()){
            if(!CruxMob.is(e, USurviveMobs.ECHO)) continue;
            PlayerDeath death = getPlayerDeathByGrave(e.getUniqueId());
            if(death==null) e.remove();
        }
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
            new ArrayList<>(event.getDrops()), totalExperience,
            System.currentTimeMillis()
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

        Collection<PlayerDeath> list = getAllDeaths(p);
        if(list.size() > 3){
            PlayerDeath oldest = getOldestDeath(list);
            deaths.remove(oldest.getWorldUUID(), oldest.getPosition().toChunkKey(), oldest.getPosition());

            Entity e = Bukkit.getEntity(oldest.getGuardianUUID());
            if(e!=null) e.remove();
        }
    }
}
