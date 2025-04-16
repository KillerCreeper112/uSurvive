package killercreepr.usurvive.core.listener;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.util.MapBuilder;
import killercreepr.cruxclaims.api.claiming.Claim;
import killercreepr.cruxclaims.api.claiming.setting.ClaimSetting;
import killercreepr.cruxclaims.api.claiming.setting.ClaimSettingSelection;
import killercreepr.cruxclaims.api.event.ClaimerClaimEvent;
import killercreepr.cruxclaims.core.claiming.setting.standard.StandardClaimSettingSelections;
import killercreepr.cruxclaims.core.claiming.setting.standard.StandardClaimSettings;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;

public class ClaimingListener implements Listener {
    protected final Map<ClaimSetting, ClaimSettingSelection> setSettings = new MapBuilder<ClaimSetting, ClaimSettingSelection>()
        .put(StandardClaimSettings.ANIMAL_SPAWNING, StandardClaimSettingSelections.ENABLED)
        .put(StandardClaimSettings.PVE, StandardClaimSettingSelections.ENABLED)
        .put(StandardClaimSettings.PVP, StandardClaimSettingSelections.ENABLED)
        .put(StandardClaimSettings.ENTITY_GRIEFING, StandardClaimSettingSelections.ENABLED)
        .put(StandardClaimSettings.LEAF_DECAY, StandardClaimSettingSelections.ENABLED)
        .put(StandardClaimSettings.MONSTER_SPAWNING, StandardClaimSettingSelections.ENABLED)
        .put(StandardClaimSettings.PLANT_GROWTH, StandardClaimSettingSelections.ENABLED)
        .put(StandardClaimSettings.FIRE_SPREAD, StandardClaimSettingSelections.ENABLED)
        .build();

    public boolean isSetSettingWorld(Key key){
        return !key.equals(Key.key("overworld"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onClaimerClaim(ClaimerClaimEvent event) {
        Claim claim = event.getClaim();
        if(!isSetSettingWorld(claim.chunk().worldKey())) return;
        boolean changed = false;
        for (Map.Entry<ClaimSetting, ClaimSettingSelection> entry : setSettings.entrySet()) {
            var setting = entry.getKey();
            var selection = entry.getValue();
            if(claim.profile().getSetting(setting) == selection) continue;
            claim.profile().setSetting(setting, selection);
            changed = true;
        }
        if(!changed) return;
        Player p = Crux.getServer().getPlayer(event.getClaimer().uuid());
        if(p == null) return;
        Lang.CLAIM_SETTINGS_CHANGED_SET.use(p);
    }

}
