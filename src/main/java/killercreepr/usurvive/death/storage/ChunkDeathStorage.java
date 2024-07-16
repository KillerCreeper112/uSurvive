package killercreepr.usurvive.death.storage;

import killercreepr.crux.data.world.ChunkBlockStorage;
import killercreepr.crux.data.world.CruxPosition;
import killercreepr.usurvive.death.DeathManager;
import killercreepr.usurvive.death.PlayerDeath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ChunkDeathStorage extends ChunkBlockStorage<PlayerDeath> {
    protected final @NotNull DeathManager deathManager;
    public ChunkDeathStorage(@NotNull Map<CruxPosition, PlayerDeath> data, @NotNull DeathManager deathManager) {
        super(data);
        this.deathManager = deathManager;
    }

    @Override
    public @NotNull CruxPosition getBlockPos(@NotNull PlayerDeath playerDeath) {
        return playerDeath.getPosition();
    }

    public void removed(@Nullable PlayerDeath death){
        if(death==null) return;
        deathManager.getEchoToPlayerDeath().remove(death.getGuardianUUID());
    }

    public void added(@NotNull PlayerDeath death){
        deathManager.getEchoToPlayerDeath().put(death.getGuardianUUID(), death);
    }

    @Override
    public PlayerDeath add(@NotNull PlayerDeath block) {
        PlayerDeath removed = super.add(block);
        removed(removed);
        added(block);
        return removed;
    }

    @Override
    public PlayerDeath remove(@NotNull PlayerDeath block) {
        PlayerDeath removed = super.remove(block);
        removed(removed);
        return removed;
    }

    @Override
    public PlayerDeath remove(@NotNull CruxPosition pos) {
        PlayerDeath removed = super.remove(pos);
        removed(removed);
        return removed;
    }
}
