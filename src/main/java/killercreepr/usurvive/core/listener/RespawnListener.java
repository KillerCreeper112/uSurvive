package killercreepr.usurvive.core.listener;

import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsManager;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettingSelections;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettings;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

public class RespawnListener implements Listener {
    protected final Plugin plugin;
    protected final ValuesProvider cfg;
    protected final PlayerSettingsManager settingsManager;

    public RespawnListener(Plugin plugin, ValuesProvider cfg, PlayerSettingsManager settingsManager) {
        this.plugin = plugin;
        this.cfg = cfg;
        this.settingsManager = settingsManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        PlayerSettingsHolder settings = settingsManager.getPlayerSettingsHolder(p);
        if(settings != null){
            if(settings.hasSetting(StandardPlayerSettings.CUSTOM_DEATH_RESPAWN, StandardPlayerSettingSelections.DISABLED)) return;
        }

        Location respawn = p.getRespawnLocation();
        if(respawn != null){
            event.setRespawnLocation(respawn);
            return;
        }
        Location spawn = cfg.RESPAWN_LOCATION().value();
        if(spawn==null) return;
        event.setRespawnLocation(spawn);
    }

}
