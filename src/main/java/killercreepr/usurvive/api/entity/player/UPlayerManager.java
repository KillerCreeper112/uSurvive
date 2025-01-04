package killercreepr.usurvive.api.entity.player;

import org.bukkit.entity.HumanEntity;

import java.util.UUID;

public interface UPlayerManager {
    /*@Nullable UPlayer getOnlinePlayer(HumanEntity player);
    @Nullable UPlayer getOnlinePlayer(UUID uuid);*/
    UPlayer getPlayer(UUID uuid);
    UPlayer getPlayer(HumanEntity player);
}
