package killercreepr.usurvive.core.entity.player.settings.standard;

import killercreepr.crux.core.Crux;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.core.registries.USurviveRegistries;

import java.util.List;

public class StandardPlayerSettings {
    public static void register(){}
    public static final PlayerSetting QUICK_ACCESS_ITEM = register(new QuickAccessSetting(
        Crux.key("quick_access_item"), StandardPlayerSettingSelections.LOCKED, List.of(
            StandardPlayerSettingSelections.LOCKED, StandardPlayerSettingSelections.UNLOCKED, StandardPlayerSettingSelections.DISABLED
        )
    ));

    public static PlayerSetting register(PlayerSetting s){
        return USurviveRegistries.PLAYER_SETTING.register(s);
    }
}
