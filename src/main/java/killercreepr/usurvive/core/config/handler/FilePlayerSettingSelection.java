package killercreepr.usurvive.core.config.handler;

import killercreepr.cruxconfig.config.common.handler.SimpleFileObjectConverter;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;

import java.lang.reflect.Type;

public class FilePlayerSettingSelection extends SimpleFileObjectConverter<PlayerSettingSelection, Key> {
    @Override
    public Type toType() {
        return Key.class;
    }

    @Override
    public PlayerSettingSelection convertTo(Key key) {
        return USurviveRegistries.PLAYER_SETTING_SELECTION.get(key);
    }

    @Override
    public Key convertFrom(PlayerSettingSelection playerSetting) {
        return playerSetting.key();
    }
}
