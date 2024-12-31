package killercreepr.usurvive.api.entity.player.settings;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public interface PlayerSettingsHolder {
    PlayerSettingSelection getSetting(@NotNull PlayerSetting setting);
    boolean hasSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection);
    void setSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection);
    @NotNull Collection<PlayerSetting> getAllAvailableSettings();
    @NotNull Map<PlayerSetting, PlayerSettingSelection> getSettings();
}
