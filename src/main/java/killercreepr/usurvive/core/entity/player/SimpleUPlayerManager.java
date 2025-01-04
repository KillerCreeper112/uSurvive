package killercreepr.usurvive.core.entity.player;

import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.api.entity.player.UPlayerManager;
import org.bukkit.entity.HumanEntity;

import java.util.UUID;

public class SimpleUPlayerManager implements UPlayerManager {
    @Override
    public UPlayer getPlayer(UUID uuid) {
        return new DataUPlayer(uuid);
    }

    @Override
    public UPlayer getPlayer(HumanEntity player) {
        return getPlayer(player.getUniqueId());
    }
}
