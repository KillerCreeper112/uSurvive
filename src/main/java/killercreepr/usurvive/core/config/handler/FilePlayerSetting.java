package killercreepr.usurvive.core.config.handler;

import killercreepr.cruxconfig.config.common.handler.SimpleFileObjectConverter;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;

import java.lang.reflect.Type;

public class FilePlayerSetting extends SimpleFileObjectConverter<PlayerSetting, Key> {
    @Override
    public Type toType() {
        return Key.class;
    }

    @Override
    public PlayerSetting convertTo(Key key) {
        return USurviveRegistries.PLAYER_SETTING.get(key);
    }

    @Override
    public Key convertFrom(PlayerSetting playerSetting) {
        return playerSetting.key();
    }
}
