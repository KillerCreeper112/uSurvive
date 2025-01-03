package killercreepr.usurvive.core.data.entity;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class SimplePlayerSettingHolder implements PlayerSettingsHolder {
    protected final UUID uuid;
    public SimplePlayerSettingHolder(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerSettingsHolder getOnline(){
        EntityMemory data = EntityMemory.get(uuid);
        if(data == null) return null;
        return data.getDataHolder(MemoryPlayerSettingsHolder.class);
    }

    public DataPlayerSettingsHolder getData(){
        DataPlayerSettingsHolder data = new DataPlayerSettingsHolder(uuid);
        data.load();
        return data;
    }

    @Override
    public PlayerSettingSelection getSetting(@NotNull PlayerSetting setting) {
        PlayerSettingsHolder data = getOnline();
        if(data != null) return data.getSetting(setting);
        return getData().getSetting(setting);
    }

    @Override
    public boolean hasSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection) {
        PlayerSettingsHolder data = getOnline();
        if(data != null) return data.hasSetting(setting, selection);
        return getData().hasSetting(setting, selection);
    }

    @Override
    public void setSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection) {
        PlayerSettingsHolder data = getOnline();
        if(data != null){
            data.setSetting(setting, selection);
            return;
        }
        DataPlayerSettingsHolder file = getData();
        file.setSetting(setting, selection);
        file.save();
    }

    @NotNull
    @Override
    public Collection<PlayerSetting> getAllAvailableSettings() {
        return USurviveRegistries.PLAYER_SETTING.values();
    }

    @NotNull
    @Override
    public Map<PlayerSetting, PlayerSettingSelection> getSettings() {
        PlayerSettingsHolder data = getOnline();
        if(data != null) return data.getSettings();
        return getData().getSettings();
    }
}
