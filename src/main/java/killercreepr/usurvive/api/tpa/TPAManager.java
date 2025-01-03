package killercreepr.usurvive.api.tpa;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TPAManager {
    @Nullable TPAHolder getTPAHolder(Entity e);
    @NotNull TPAHolder getOrCreateTPAHolder(Entity e);

    void acceptTPA(Entity sender, TPARequest request);
}
