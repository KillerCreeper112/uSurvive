package killercreepr.usurvive.api.entity.player.settings;

import org.bukkit.entity.HumanEntity;

import java.util.UUID;

public interface PlayerSettingsManager {
    PlayerSettingsHolder getPlayerSettingsHolder(HumanEntity p);
    PlayerSettingsHolder getPlayerSettingsHolder(UUID uuid);
}
