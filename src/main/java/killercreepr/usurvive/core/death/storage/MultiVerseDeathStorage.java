package killercreepr.usurvive.core.death.storage;

import killercreepr.crux.api.data.world.MultiVerseWorldStorage;
import killercreepr.crux.api.data.world.WorldChunkStorage;
import killercreepr.usurvive.api.death.DeathManager;
import killercreepr.usurvive.core.death.PlayerDeath;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MultiVerseDeathStorage extends MultiVerseWorldStorage<PlayerDeath> {
    protected final @NotNull DeathManager deathManager;
    public MultiVerseDeathStorage(@NotNull Map<UUID, WorldChunkStorage<PlayerDeath>> data, @NotNull DeathManager deathManager) {
        super(data);
        this.deathManager = deathManager;
    }

    @Override
    public @NotNull WorldChunkStorage<PlayerDeath> newWorldStorage() {
        return new WorldDeathStorage(new HashMap<>(), deathManager);
    }
}
