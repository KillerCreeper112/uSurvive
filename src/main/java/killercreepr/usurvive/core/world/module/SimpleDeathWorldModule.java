package killercreepr.usurvive.core.world.module;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.data.world.ChunkBlockStorage;
import killercreepr.crux.api.data.world.WorldChunkStorage;
import killercreepr.crux.core.Crux;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.file.CruxJson;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.world.module.SimpleWorldModule;
import killercreepr.usurvive.api.event.PlayerDeathExpireEvent;
import killercreepr.usurvive.api.world.module.DeathWorldModule;
import killercreepr.usurvive.core.death.PlayerDeath;
import killercreepr.usurvive.core.death.storage.WorldDeathStorage;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Predicate;

public class SimpleDeathWorldModule extends SimpleWorldModule implements DeathWorldModule {
    protected final WorldDeathStorage deaths;
    public SimpleDeathWorldModule(@NotNull CruxWorld parent, WorldDeathStorage deaths) {
        super(parent);
        this.deaths = deaths;
    }

    @Override
    public void onInitiate() {
        super.onInitiate();
        deaths.getDeathManager().addDeathWorldModule(this);
        load();
    }

    @Override
    public void onUnload(boolean save) {
        super.onUnload(save);
        deaths.getDeathManager().removeDeathWorldModule(this);
        if(!save) return;
        save();
    }

    @Override
    public void onDelete() {
        super.onDelete();
        deaths.getDeathManager().removeDeathWorldModule(this);
        UUID uuid = parent.getUUID();
        try{
            FileUtils.deleteDirectory(new CruxFolder(CruxCore.inst(), "data/usurvive/deaths/" + uuid).file());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void save(){
        save(deaths);
    }

    public void save(@NotNull WorldChunkStorage<PlayerDeath> chunk){
        UUID uuid = parent.getUUID();
        try{
            FileUtils.deleteDirectory(new CruxFolder(CruxCore.inst(), "data/usurvive/deaths/" + uuid).file());
        }catch (IOException e){
            e.printStackTrace();
        }
        chunk.getData().forEach((key, block) ->{
            save(uuid, key, block);
        });
    }

    public void save(@NotNull UUID world, long chunk, @NotNull ChunkBlockStorage<PlayerDeath> block){
        CruxJson file = new CruxJson(CruxCore.inst(), "data/usurvive/deaths/" + world + "/" + chunk);
        file.serialize("values", block.getData().values());
        file.save();
    }

    public void load(){
        File[] files = new CruxFolder(CruxCore.inst(), "deaths").file().listFiles();
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
        list.forEach(death -> deaths.add(death.getPosition().toChunkKey(), death));
    }

    @NotNull
    @Override
    public Collection<PlayerDeath> getDeaths(@Nullable Predicate<PlayerDeath> filter) {
        Collection<PlayerDeath> list = new HashSet<>();
        deaths.removeIf(chunk ->{
            chunk.removeIf(death ->{
                if(death.hasExpired()){
                    PlayerDeathExpireEvent expire = new PlayerDeathExpireEvent(!Crux.getServer().isPrimaryThread(), deaths.getDeathManager(), death);
                    if(expire.callEvent()) return true;
                }
                if(filter != null && !filter.test(death)) return false;
                list.add(death);
                return false;
            });
            return chunk.isEmpty();
        });
        return list;
    }

    @NotNull
    @Override
    public Collection<PlayerDeath> getPlayerDeaths(@NotNull UUID player) {
        return getDeaths(e -> e.getPlayerUUID().equals(player));
    }

    @Override
    public boolean addDeath(@NotNull PlayerDeath death) {
        deaths.add(death.getGravePosition().toChunkKey(), death);
        return true;
    }

    @Override
    public boolean removeDeath(@NotNull PlayerDeath death) {
        return deaths.remove(death.getGravePosition().toChunkKey(), death.getGravePosition()) != null;
    }

    @NotNull
    @Override
    public WorldDeathStorage getDeathStorage() {
        return deaths;
    }
}
