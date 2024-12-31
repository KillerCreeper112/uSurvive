package killercreepr.usurvive.core.data;

import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimplePlayerSettingsCache implements PlayerSettingsHolder {
    protected final Map<PlayerSetting, PlayerSettingSelection> map = new HashMap<>();
    @Override
    public PlayerSettingSelection getSetting(@NotNull PlayerSetting setting) {
        return map.getOrDefault(setting, setting.defaultSelection());
    }

    @Override
    public boolean hasSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection) {
        return selection.equals(getSetting(setting));
    }

    @Override
    public void setSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection) {
        if(selection.equals(getSetting(setting))){
            map.remove(setting);
        }else map.put(setting, selection);
    }

    @NotNull
    @Override
    public Collection<PlayerSetting> getAllAvailableSettings() {
        return USurviveRegistries.PLAYER_SETTING.values();
    }

    @NotNull
    @Override
    public Map<PlayerSetting, PlayerSettingSelection> getSettings() {
        return map;
    }
}
