package killercreepr.usurvive.core.entity.player.settings.standard;

import killercreepr.crux.core.Crux;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.core.registries.USurviveRegistries;

public class StandardPlayerSettingSelections {
    public static final PlayerSettingSelection ENABLED = register(PlayerSettingSelection.playerSettingSelection(Crux.key("enabled")));
    public static final PlayerSettingSelection DISABLED = register(PlayerSettingSelection.playerSettingSelection(Crux.key("disabled")));

    public static final PlayerSettingSelection LOCKED = register(PlayerSettingSelection.playerSettingSelection(Crux.key("locked")));
    public static final PlayerSettingSelection UNLOCKED = register(PlayerSettingSelection.playerSettingSelection(Crux.key("unlocked")));

    public static PlayerSettingSelection register(PlayerSettingSelection s){
        return USurviveRegistries.PLAYER_SETTING_SELECTION.register(s);
    }
}
