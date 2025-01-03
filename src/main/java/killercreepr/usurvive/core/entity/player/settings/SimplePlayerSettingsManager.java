package killercreepr.usurvive.core.entity.player.settings;

import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsManager;
import killercreepr.usurvive.core.data.entity.SimplePlayerSettingHolder;
import org.bukkit.entity.HumanEntity;

import java.util.UUID;

public class SimplePlayerSettingsManager implements PlayerSettingsManager {
    @Override
    public PlayerSettingsHolder getPlayerSettingsHolder(HumanEntity p) {
        return getPlayerSettingsHolder(p.getUniqueId());
    }

    @Override
    public PlayerSettingsHolder getPlayerSettingsHolder(UUID uuid) {
        return new SimplePlayerSettingHolder(uuid);
    }
}
