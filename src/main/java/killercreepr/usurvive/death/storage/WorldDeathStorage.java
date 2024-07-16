package killercreepr.usurvive.death.storage;

import killercreepr.crux.data.world.ChunkBlockStorage;
import killercreepr.crux.data.world.WorldChunkStorage;
import killercreepr.usurvive.death.DeathManager;
import killercreepr.usurvive.death.PlayerDeath;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WorldDeathStorage extends WorldChunkStorage<PlayerDeath> {
    protected final @NotNull DeathManager deathManager;
    public WorldDeathStorage(@NotNull Map<Long, ChunkBlockStorage<PlayerDeath>> data, @NotNull DeathManager deathManager) {
        super(data);
        this.deathManager = deathManager;
    }

    @Override
    public @NotNull ChunkBlockStorage<PlayerDeath> newChunkStorage() {
        return new ChunkDeathStorage(new HashMap<>(), deathManager);
    }
}
