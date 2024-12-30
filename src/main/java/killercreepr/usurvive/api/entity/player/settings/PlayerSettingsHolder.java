package killercreepr.usurvive.api.entity.player.settings;

import org.jetbrains.annotations.NotNull;

public interface PlayerSettingsHolder {
    PlayerSettingSelection getSetting(@NotNull PlayerSetting setting);
    boolean hasSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection);
    void setSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection);
}
