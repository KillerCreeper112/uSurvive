package killercreepr.usurvive.core.data.entity;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerDataHolder;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SimplePlayerSettingsHolder extends PlayerDataHolder implements PlayerSettingsHolder {
    public static final Key KEY = Crux.key("player_settings");
    public SimplePlayerSettingsHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
    }

    public SimplePlayerSettingsHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    protected final @NotNull Map<PlayerSetting, PlayerSettingSelection> settings = new HashMap<>();

    public PlayerSettingSelection getSetting(@NotNull PlayerSetting setting){
        return settings.getOrDefault(setting, setting.defaultSelection());
    }

    public boolean hasSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection){
        return selection.compare(getSetting(setting));
    }

    @Override
    public void setSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection) {
        if(selection.equals(getSetting(setting))) return;
        if(setting.defaultSelection().equals(selection)){
            settings.remove(setting);
        }else settings.put(setting, selection);

        HumanEntity found = getPlayer();
        if(found != null){
            setting.onSettingChanged(found, getSetting(setting));
        }
    }
}
