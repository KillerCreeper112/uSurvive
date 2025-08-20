package killercreepr.usurvive.core.world.module;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.data.tick.Ticked;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
import killercreepr.cruxworlds.core.world.module.SimpleWorldModule;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ResetWorldModule extends SimpleWorldModule implements Ticked {
    protected final Holder<NumberProvider> resetTime;
    protected final CruxWorldType type;
    protected final Map<Key, CruxWorldType> linkedWorlds;
    public ResetWorldModule(@NotNull CruxWorld parent, Holder<NumberProvider>  resetTime, CruxWorldType type, Map<Key, CruxWorldType> linkedWorlds) {
        super(parent);
        this.resetTime = resetTime;
        this.type = type;
        this.linkedWorlds = linkedWorlds;
    }

    protected int tick = 0;
    protected boolean resetting = false;
    //1 hour = 72,000
    @Override
    public void tick() {
        if(resetting) return;
        tick++;
        //every 5 seconds, let the players know.
        if(tick % 100 == 0){
            long time = getResettingIn();
            if(time > 5 && time < 3600000){
                var tags = TagContainer.merged().add(Tag.parsed("resetting_in", time + ""))
                    .add(Tag.parsed("reset_time", getResetTimeAt() + ""))
                    .add(Tag.parsed("last_reset", getLastReset() + ""));
                forEachAllPlayers(p ->{
                    Lang.RESOURCE_WORLD_RESETTING_IN_TICK.use(p, tags);
                });
            }
        }

        //after 30 seconds of init or every hour, check.
        if(tick == 600 || tick >= 72000){
            if(tick != 600) tick = 600;
            check();
        }
    }

    public void forEachAllPlayers(Consumer<Player> consumer){
        parent.toBukkitWorld().getPlayers().forEach(consumer);
        CruxWorldManager worldManager = CruxCore.core().worldManager();
        for(Key key : linkedWorlds.keySet()){
            CruxWorld crux = worldManager.getWorld(key);
            if(crux == null) continue;
            crux.toBukkitWorld().getPlayers().forEach(consumer);
        }
    }

    public long getLastReset(){
        return CruxTag.get(parent.toBukkitWorld(), "last_reset", PersistentDataType.LONG, 0L);
    }

    public long getResetTimeAt(){
        int resetTime = this.resetTime.value().value().intValue();
        return getLastReset() + (resetTime*50L);
    }

    public long getResettingIn(){
        int resetTime = this.resetTime.value().value().intValue();
        long lastReset = getLastReset();

        long resetTimeInMillis = resetTime * 50L;  // Minecraft runs at 20 ticks per second (1000ms / 20 = 50ms per tick)

        // Calculate the time remaining until the reset
        long currentTime = System.currentTimeMillis();
        long timeElapsedSinceLastReset = currentTime - lastReset;  // in milliseconds

        // The time remaining until the reset
        long timeRemaining = resetTimeInMillis - timeElapsedSinceLastReset;

        // Return the time remaining
        return timeRemaining < 0L ? 0 : timeRemaining;
    }

    public void check(){
        int resetTime = this.resetTime.value().value().intValue();
        long lastReset = getLastReset();
        if(CruxMath.hasOccurredWithin(lastReset, resetTime)){
            long time = getResettingIn();
            var tags = TagContainer.merged().add(Tag.parsed("resetting_in", time + ""));
            forEachAllPlayers(p ->{
                Lang.RESOURCE_WORLD_RESETTING_IN.use(p, tags);
            });
            return;
        }
        forEachAllPlayers(Lang.RESOURCE_WORLD_RESETTING::use);
        resetting = true;
        Crux.scheduler().runTask(this::reset);
    }

    public void teleportPlayersToSpawn(World world){
        var cfg = USurvivePlugin.inst().values();
        World spawnWorld = Crux.getServer().getWorld(cfg.SPAWN_WORLD().valueOr("a"));
        if(spawnWorld == null) return;
        for (Player p : world.getPlayers()) {
            p.teleport(spawnWorld.getSpawnLocation());
        }
    }

    public void reset(){
        CruxWorldManager worldManager = CruxCore.core().worldManager();
        for(Key key : linkedWorlds.keySet()){
            CruxWorld crux = worldManager.getWorld(key);
            if(crux == null) continue;
            teleportPlayersToSpawn(crux.toBukkitWorld());
        }
        teleportPlayersToSpawn(parent.toBukkitWorld());

        reset(parent, type);
        linkedWorlds.forEach((key, type) ->{
            worldManager.loadWorld(key).whenComplete((world, throwable) ->{
                if(throwable != null) throwable.printStackTrace();
                if(world == null) return;
                reset(world, type);
            });
        });
    }

    public void reset(CruxWorld parent, CruxWorldType type){
        CruxWorldManager worldManager = CruxCore.core().worldManager();
        Crux.log(Level.INFO, "[ResetWorldModule] Resetting world, " + parent.key() + "...");
        worldManager.deleteWorld(parent).whenComplete((value, throwable) ->{
            if(throwable != null) Crux.log(Level.SEVERE, throwable.getMessage());
            if(!value){
                Crux.log(Level.SEVERE, "[ResetWorldModule] COULD NOT DELETE " + parent.key() + ".");
                return;
            }
            CruxWorld created = worldManager.getOrCreateWorld(type);
            if(created != null){
                CruxTag.set(created.toBukkitWorld(), "last_reset", PersistentDataType.LONG, System.currentTimeMillis());
                Crux.log(Level.INFO, "[ResetWorldModule] World, " + parent.key() + " has been reset.");
            }else{
                Crux.log(Level.SEVERE, "[ResetWorldModule] COULD NOT CREATE NEW " + parent.key() + ".");
            }
        });
    }
}
