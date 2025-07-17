package killercreepr.usurvive.core.guide;

import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsManager;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettingSelections;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettings;
import org.bukkit.entity.Player;

public class GuideUtil {
    public static boolean hasGuideDisabled(Player p){
        PlayerSettingsManager manager = USurvivePlugin.inst().getPlayerSettingsManager();
        var holder = manager.getPlayerSettingsHolder(p);
        return holder != null && holder.hasSetting(StandardPlayerSettings.GUIDE, StandardPlayerSettingSelections.DISABLED);
    }
}
