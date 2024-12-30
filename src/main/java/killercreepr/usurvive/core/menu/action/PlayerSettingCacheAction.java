package killercreepr.usurvive.core.menu.action;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.cruxmenus.api.menu.contex.ActionContext;
import killercreepr.cruxmenus.core.menu.action.SimpleMenuAction;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsCache;
import killercreepr.usurvive.core.data.SimplePlayerSettingsCache;
import killercreepr.usurvive.core.data.entity.SimplePlayerSettingsHolder;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class PlayerSettingCacheAction extends SimpleMenuAction {
    public PlayerSettingCacheAction(@NotNull Key key) {
        super(key);
    }

    @Override
    public boolean execute(@NotNull ActionContext ctx, @NotNull String[] args) {
        HumanEntity p = ctx.getPlayer();
        SimplePlayerSettingsHolder holder = EntityMemory.getOrCreateDataHolder(p, SimplePlayerSettingsHolder.class);
        if(holder == null) return false;
        String action = args[0];
        switch (action.toLowerCase()){
            case "set" -> {
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(Crux.key(args[1]));
                PlayerSettingSelection selection = USurviveRegistries.PLAYER_SETTING_SELECTION.get(Crux.key(args[2]));
                PlayerSettingsCache cache = holder.getCache();
                if(cache == null){
                    cache = new SimplePlayerSettingsCache();
                    holder.setCache(cache);
                }
                cache.setSetting(setting, selection);
            }
        }
        return true;
    }
}
