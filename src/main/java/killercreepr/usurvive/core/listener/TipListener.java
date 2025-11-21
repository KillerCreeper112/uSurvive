package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsManager;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettingSelections;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettings;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.registries.TipCategoryRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class TipListener implements Listener {
    protected final PlayerSettingsManager settingsManager;
    protected final TipCategoryRegistry tips;

    public TipListener(PlayerSettingsManager settingsManager, TipCategoryRegistry tips) {
        this.settingsManager = settingsManager;
        this.tips = tips;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(event.getReviveHealth() > 0D) return;
        var p = event.getPlayer();
        var settings = settingsManager.getPlayerSettingsHolder(p);
        if(settings != null){
            if(settings.hasSetting(StandardPlayerSettings.TIPS, StandardPlayerSettingSelections.DISABLED)) return;
        }
        var tip = tips.randomTip();
        if(tip == null) return;
        Lang.TIP_MESSAGE.use(p, TagContainer.merged().hook(p).add(Tag.parsed("tip_message", tip.message())));
    }

}
