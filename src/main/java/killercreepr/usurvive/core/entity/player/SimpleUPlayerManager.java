package killercreepr.usurvive.core.entity.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.api.entity.player.UPlayerManager;
import org.bukkit.entity.HumanEntity;

import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SimpleUPlayerManager implements UPlayerManager {
    protected final Cache<UUID, UPlayer> cache = CacheBuilder.newBuilder()
        .expireAfterAccess(60, TimeUnit.MINUTES)
        .maximumSize(1_000)
        //.softValues()
        .build();

    @Override
    public UPlayer getPlayer(UUID uuid) {
        UPlayer player = cache.getIfPresent(uuid);
        if(player != null) return player;
        player = new DataUPlayer(uuid);
        cache.put(uuid, player);
        player.load();
        return player;
    }

    @Override
    public UPlayer getPlayer(HumanEntity p) {
        UPlayer player = cache.getIfPresent(p.getUniqueId());
        if(player != null) return player;
        player = new ReferenceUPlayer(p.getUniqueId(), new WeakReference<>(p));
        cache.put(player.getUUID(), player);
        player.load();
        return player;
        //return getPlayer(player.getUniqueId());
    }
}
