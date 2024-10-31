package killercreepr.usurvive.death;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import killercreepr.crux.data.entity.EntityMemory;
import killercreepr.crux.data.world.ChunkBlockStorage;
import killercreepr.crux.data.world.CruxPosition;
import killercreepr.crux.data.world.WorldChunkStorage;
import killercreepr.crux.util.CruxGoalUtil;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.file.CruxJson;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.usurvive.data.entity.DeathHolder;
import killercreepr.usurvive.death.storage.MultiVerseDeathStorage;
import killercreepr.usurvive.entity.USurviveMobs;
import killercreepr.usurvive.entity.mob.goals.EchoGoal;
import killercreepr.usurvive.event.PlayerDeathExpireEvent;
import killercreepr.usurvive.event.PlayerEchoDeathEvent;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class DeathManager implements Listener {
    protected final @NotNull Plugin plugin;

    public DeathManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    protected final MultiVerseDeathStorage deaths = new MultiVerseDeathStorage(
        new HashMap<>(), this
    );
    protected final Map<UUID, PlayerDeath> echoToPlayerDeath = new HashMap<>();
    public @NotNull Collection<PlayerDeath> getAllDeaths(@Nullable Predicate<PlayerDeath> filter){
        Collection<PlayerDeath> list = new HashSet<>();
        deaths.forEach(world ->{
            world.forEach(chunk ->{
                chunk.removeIf(l ->{
                    if(l.hasExpired()){
                        PlayerDeathExpireEvent expire = new PlayerDeathExpireEvent(this, l);
                        if(expire.callEvent()) return true;
                    }
                    if(filter != null && !filter.test(l)) return false;
                    list.add(l);
                    return false;
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
            s.removeIf(death ->{
                if(death.hasExpired()){
                    PlayerDeathExpireEvent expire = new PlayerDeathExpireEvent(this, death);
                    if(expire.callEvent()) return true;
                }
                if(death.getPlayerUUID().equals(player)) list.add(death);
                return false;
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
            if(death==null)  e.remove();
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

        DeathHolder deathHolder = EntityMemory.getOrCreateDataHolder(p, DeathHolder.class);

        Entity echo = USurviveMobs.ECHO.spawn(p.getLocation());

        PlayerDeath death = new PlayerDeath(
            p.getUniqueId(), p.getWorld().getUID(),
            CruxPosition.location(p.getLocation()), echo.getUniqueId(),
            new ArrayList<>(event.getDrops()), totalExperience,
            System.currentTimeMillis(), deathHolder.getDeathExpiryTime()
        );
        if(echo instanceof Mob m){
            EchoGoal goal = CruxGoalUtil.getGoal(m, EchoGoal.class, EchoGoal.defaultKey());
            if(goal != null) goal.firstSetup(death);
        }

        PlayerEchoDeathEvent deathEvent = new PlayerEchoDeathEvent(this, death);
        if(!deathEvent.callEvent()){
            echo.remove();
            return;
        }
        PlayerDeath newDeath = deathEvent.getDeath();
        if(!newDeath.getGuardianUUID().equals(death.getGuardianUUID())) echo.remove();
        else if(!newDeath.equals(death)){
            if(echo instanceof Mob m){
                EchoGoal goal = CruxGoalUtil.getGoal(m, EchoGoal.class, EchoGoal.defaultKey());
                if(goal != null) goal.firstSetup(newDeath);
            }
        }

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

    public void save(){
        deaths.getData().forEach(this::save);
    }

    public void save(@NotNull UUID world, @NotNull WorldChunkStorage<PlayerDeath> chunk){
        try{
            FileUtils.deleteDirectory(new CruxFolder(plugin, "data/usurvive/deaths/" + world).file());
        }catch (IOException ignored){}
        chunk.getData().forEach((key, block) ->{
            save(world, key, block);
        });
    }

    public void save(@NotNull UUID world, long chunk, @NotNull ChunkBlockStorage<PlayerDeath> block){
        CruxJson file = new CruxJson(plugin, "data/usurvive/deaths/" + world + "/" + chunk);
        file.serialize("values", block.getData().values());
        /*FileArray a = new FileArray(block.getData().size());
        block.getData().forEach((key, death) ->{
            a.add(file.fileRegistry().serializeToFile(death));
        });
        file.setElement("values", a.toYaml());*/
        file.save();
    }

    public void load(){
        File[] files = new CruxFolder(plugin, "deaths").file().listFiles();
        if(files==null) return;
        for(File f : files){
            load(f);
        }
    }

    public void load(@NotNull File worldFolder){
        File[] files = worldFolder.listFiles();
        if(files==null) return;
        for(File f : files){
            load(new CruxJson(f));
        }
    }

    public void load(@NotNull CruxJson chunkFile){
        Collection<PlayerDeath> list = chunkFile.deserialize("values", new TypeToken<Collection<PlayerDeath>>(){}.getType());
        if(list == null) return;
        list.forEach(death -> deaths.add(
            death.getWorldUUID(),
            death.getPosition().toChunkKey(),
            death
        ));
        /*if(!(chunkFile.getAsYamlObject("values") instanceof YamlArray a)) return;
        a.forEach(ele ->{
            PlayerDeath death = chunkFile.yamlRegistry().deserialize(PlayerDeath.class, ele);
            if(death==null) return;
            deaths.add(
                death.getWorldUUID(),
                death.getPosition().toChunkKey(),
                death
            );
        });*/
    }
}
