package killercreepr.usurvive.core.tpa;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.usurvive.api.tpa.TPAHolder;
import killercreepr.usurvive.api.tpa.TPAManager;
import killercreepr.usurvive.api.tpa.TPARequest;
import killercreepr.usurvive.core.data.entity.SimpleTPAHolder;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleTPAManager implements TPAManager {
    @Nullable
    @Override
    public TPAHolder getTPAHolder(Entity e) {
        return EntityMemory.getDataHolder(e, SimpleTPAHolder.class);
    }

    @NotNull
    @Override
    public TPAHolder getOrCreateTPAHolder(Entity e) {
        return EntityMemory.getOrCreateDataHolder(
            e, SimpleTPAHolder.class, SimpleTPAHolder::new
        );
    }

    @Override
    public void acceptTPA(Entity sender, TPARequest request) {
        request.onAccept(sender);
    }
}
