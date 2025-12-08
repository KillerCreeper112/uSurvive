package killercreepr.usurvive.core.death;

import com.google.common.base.Preconditions;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxstats.api.bukkit.BukkitStatHolder;
import killercreepr.cruxworlds.api.event.CruxWorldPreCreateEvent;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.creator.CruxWorldModuleCreator;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import killercreepr.usurvive.api.death.DeathManager;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsManager;
import killercreepr.usurvive.api.event.PlayerEchoDeathEvent;
import killercreepr.usurvive.api.event.PrePlayerUDeathEvent;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.api.world.module.DeathWorldModule;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.data.entity.DeathHolder;
import killercreepr.usurvive.core.death.storage.WorldDeathStorage;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.entity.mob.goals.EchoGoal;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettingSelections;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettings;
import killercreepr.usurvive.core.stat.USurviveStats;
import killercreepr.usurvive.core.world.module.SimpleDeathWorldModule;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Level;

public class SimpleDeathManager implements DeathManager, Listener {
    protected final @NotNull Plugin plugin;
    protected final @NotNull CruxWorldManager worldManager;
    protected final @NotNull PlayerSettingsManager settingsManager;

    public SimpleDeathManager(@NotNull Plugin plugin, @NotNull CruxWorldManager worldManager, @NotNull PlayerSettingsManager settingsManager) {
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.settingsManager = settingsManager;
    }

    /*protected final MultiVerseDeathStorage deaths = new MultiVerseDeathStorage(
        new HashMap<>(), this
    );*/
    protected final Collection<DeathWorldModule> hookedModules = new HashSet<>();
    protected final Map<UUID, PlayerDeath> echoToPlayerDeath = new HashMap<>();
    /*public @NotNull Collection<PlayerDeath> getAllDeaths(@Nullable Predicate<PlayerDeath> filter){
        Collection<PlayerDeath> list = new HashSet<>();
        deaths.forEach(world ->{
            world.forEach(chunk ->{
                chunk.removeIf(l ->{
                    if(l.hasExpired()){
                        PlayerDeathExpireEvent expire = new PlayerDeathExpireEvent(!Crux.getServer().isPrimaryThread(),this, l);
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
                    PlayerDeathExpireEvent expire = new PlayerDeathExpireEvent(!Crux.getServer().isPrimaryThread(),this, death);
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
    }*/

    @Override
    public @Nullable PlayerDeath getPlayerDeathByGuardian(@NotNull UUID echo){
        return echoToPlayerDeath.get(echo);
    }

    public @NotNull Map<UUID, PlayerDeath> getEchoToPlayerDeath() {
        return echoToPlayerDeath;
    }

    @Override
    public boolean addDeathWorldModule(DeathWorldModule module) {
        return hookedModules.add(module);
    }

    @Override
    public boolean removeDeathWorldModule(DeathWorldModule module) {
        return hookedModules.remove(module);
    }

    @Override
    public Collection<DeathWorldModule> getHookedDeathWorldModules() {
        return hookedModules;
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
            PlayerDeath death = getPlayerDeathByGuardian(e.getUniqueId());
            if(death==null) e.remove();
        }
    }

    public CompletableFuture<Block> findGravePositionAsync(Block center, DeathWorldModule module){
        return CompletableFuture.supplyAsync(() -> findGravePosition(center, module));
    }

    public Block findGravePosition(Block center, DeathWorldModule module){
        ValuesProvider cfg = USurvivePlugin.inst().values();
        return findGravePosition(center, module, cfg.DEATH_GRAVE_SPAWN_SEARCH_RANGE().value().intValue());
    }

    public static List<Block> getNearbyBlocks(Block center, int radius) {
        List<Block> list = new ArrayList<>();

        for(int x = radius; x >= -radius; --x) {
            for(int y = radius; y >= -radius; --y) {
                for(int z = radius; z >= -radius; --z) {
                    list.add(center.getRelative(x, y, z));
                }
            }
        }

        return list;
    }

    public Block findGravePosition(Block center, DeathWorldModule module, int range){
        if(isValidGravePosition(center, module)) return center;
        List<Block> nearbyBlocks = getNearbyBlocks(center, range);
        nearbyBlocks.sort((b1, b2) -> {
            double dist1 = center.getLocation().distanceSquared(b1.getLocation());
            double dist2 = center.getLocation().distanceSquared(b2.getLocation());
            return Double.compare(dist1, dist2);
        });
        for(Block b : nearbyBlocks){
            if(isValidGravePosition(b, module)) return b;
        }
        World world = center.getWorld();
        Block b = world.getHighestBlockAt(center.getX(), center.getZ());
        if(isValidGravePosition(b, module)) return b;
        for(int x = -range; x <= range; x++){
            for(int z = -range; z <= range; z++){
                if(x == 0 && z == 0) continue;
                b = world.getHighestBlockAt(center.getX() + x, center.getZ() + z);
                if(isValidGravePosition(b, module)) return b;
            }
        }
        return null;
    }

    public boolean isDangerousLiquid(Block b){
        return b.getType() == Material.LAVA;
    }

    public boolean isValidGravePosition(Block b, DeathWorldModule module){
        if(!b.isEmpty() && !b.isPassable()) return false;
        if(isDangerousLiquid(b)) return false;
        Block ground = b.getRelative(BlockFace.DOWN);
        if(!(ground.isSolid() && !isDangerousLiquid(ground))) return false;
        return module.getDeathStorage().get(b.getChunk().getChunkKey(), CruxPosition.block(b.getLocation())) == null;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getPlayer();
        World deathWorld = p.getWorld();

        PlayerSettingsHolder settings = settingsManager.getPlayerSettingsHolder(p);
        if(settings != null){
            if(settings.hasSetting(StandardPlayerSettings.SPAWN_GRAVESTONE, StandardPlayerSettingSelections.DISABLED)) return;
        }

        int maxGravestones = (int) BukkitStatHolder.holder(p).getStatValue(USurviveStats.MAX_GRAVESTONES);
        if(maxGravestones < 1) return;

        CruxWorld crux = worldManager.getWorld(deathWorld.key());
        if(crux == null) return;
        DeathWorldModule module = crux.getModule(DeathWorldModule.class);
        if(module == null) return;

        PrePlayerUDeathEvent preEvent = new PrePlayerUDeathEvent(p, this, event);
        if(!preEvent.callEvent()){
            return;
        }

        int totalExperience = p.calculateTotalExperiencePoints();

        if(event.getDrops().isEmpty() && totalExperience < 1){
            event.getDrops().clear();
            event.setDroppedExp(0);
            return;
        }
        long time = System.currentTimeMillis();


        Location deathLocation = p.getLocation().clone();
        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        event.getDrops().clear();
        event.setDroppedExp(0);

        DeathHolder deathHolder = EntityMemory.getOrCreateDataHolder(p, DeathHolder.class);
        UPlayer uPlayer = UPlayer.getPlayer(p);
        if(uPlayer != null){
            uPlayer.addRecentDeaths(1);
        }

        findGravePositionAsync(deathLocation.getBlock(), module).whenComplete((graveBlock, throwable) ->{
            if(throwable != null) Crux.log(Level.SEVERE, throwable.getMessage());
            Crux.scheduler().runTask(() ->{
                Block finalGraveBlock = graveBlock;
                if(finalGraveBlock == null) finalGraveBlock = deathLocation.getBlock();

                CruxPosition gravePosition = CruxPosition.location(finalGraveBlock.getLocation().toCenterLocation().subtract(0, .4, 0));

                Entity echo = USurviveMobs.ECHO.spawn(gravePosition.toLocation(deathWorld));

                PlayerDeath death = new PlayerDeath(
                    p.getUniqueId(), deathWorld.key(),
                    CruxPosition.location(deathLocation),
                    gravePosition,
                    echo.getUniqueId(),
                    new ArrayList<>(drops), totalExperience,
                    time, deathHolder.getDeathExpiryTime()
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
                addDeath(newDeath);
                //deaths.add(newDeath.getWorldUUID(), newDeath.getGravePosition().toChunkKey(), newDeath);
                /*event.getDrops().clear();
                event.setDroppedExp(0);*/

                Collection<PlayerDeath> list = getAllDeaths(p);
                if(list.size() > maxGravestones){
                    PlayerDeath oldest = getOldestDeath(list);

                    World world = oldest.getWorld();
                    if(world != null){
                        int chunkX = oldest.getPosition().toChunkX();
                        int chunkZ = oldest.getPosition().toChunkZ();
                        world.getChunkAtAsync(chunkX, chunkZ).whenComplete(((chunk, throwable2) -> {
                            Entity e = plugin.getServer().getEntity(oldest.getGuardianUUID());
                            if(e!=null){
                                if(e instanceof LivingEntity d) d.damage(99999999D, DamageSource.builder(DamageType.GENERIC_KILL).build());
                                else e.remove();
                            }
                            removeDeath(oldest);
                            //deaths.remove(oldest.getWorldUUID(), oldest.getPosition().toChunkKey(), oldest.getPosition());
                        }));
                    }else{
                        Crux.log(Level.WARNING, "PlayerDeath world, " + oldest.getWorldKey() + " was not loaded. From player: " + oldest.getPlayerUUID());
                        Entity e = plugin.getServer().getEntity(oldest.getGuardianUUID());
                        if(e!=null){
                            if(e instanceof LivingEntity d) d.damage(99999999D, DamageSource.builder(DamageType.GENERIC_KILL).build());
                            else e.remove();
                        }
                        removeDeath(oldest);
                        //deaths.remove(oldest.getWorldUUID(), oldest.getPosition().toChunkKey(), oldest.getPosition());
                    }
                }
            });
        });
    }

    protected final CruxWorldModuleCreator moduleCreator = world ->
        new SimpleDeathWorldModule(world, new WorldDeathStorage(new ConcurrentHashMap<>(), this));
    @EventHandler(ignoreCancelled = true)
    public void onCruxWorldPreCreate(CruxWorldPreCreateEvent event) {
        event.getModuleCreators().add(moduleCreator);
    }

    @NotNull
    @Override
    public Collection<PlayerDeath> getAllDeaths(@Nullable Predicate<PlayerDeath> filter) {
        Collection<PlayerDeath> list = new HashSet<>(echoToPlayerDeath.values());
        if(filter != null) list.removeIf(e -> !filter.test(e));
        return list;
    }

    @NotNull
    @Override
    public Collection<PlayerDeath> getAllDeathsInWorld(@NotNull UUID player, @NotNull Key world) {
        CruxWorld crux = worldManager.getWorld(world);
        if(crux == null) return new HashSet<>();
        DeathWorldModule module = crux.getModule(DeathWorldModule.class);
        if(module == null) return new HashSet<>();
        return module.getPlayerDeaths(player);
    }

    @NotNull
    @Override
    public Collection<PlayerDeath> getAllDeaths(@NotNull UUID player) {
        return getAllDeaths(e -> e.getPlayerUUID().equals(player));
    }

    @Override
    public boolean addDeath(@NotNull PlayerDeath death) {
        CruxWorld world = worldManager.getWorld(death.getWorldKey());
        if(world == null) return false;
        DeathWorldModule module = world.getModule(DeathWorldModule.class);
        if(module == null) return false;
        return module.addDeath(death);
    }

    @Override
    public boolean removeDeath(@NotNull PlayerDeath death) {
        CruxWorld world = worldManager.getWorld(death.getWorldKey());
        if(world == null) return false;
        DeathWorldModule module = world.getModule(DeathWorldModule.class);
        if(module == null) return false;
        return module.removeDeath(death);
    }

    /*public void save(){
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
        *//*FileArray a = new FileArray(block.getData().size());
        block.getData().forEach((key, death) ->{
            a.add(file.fileRegistry().serializeToFile(death));
        });
        file.setElement("values", a.toYaml());*//*
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
        *//*if(!(chunkFile.getAsYamlObject("values") instanceof YamlArray a)) return;
        a.forEach(ele ->{
            PlayerDeath death = chunkFile.yamlRegistry().deserialize(PlayerDeath.class, ele);
            if(death==null) return;
            deaths.add(
                death.getWorldUUID(),
                death.getPosition().toChunkKey(),
                death
            );
        });*//*
    }*/
}
