package killercreepr.usurvive.core.data.entity;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.core.Crux;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataPlayerSettingsHolder implements PlayerSettingsHolder, Loadable {
    protected final @NotNull Map<PlayerSetting, PlayerSettingSelection> settings = new HashMap<>();
    protected final UUID uuid;

    public DataPlayerSettingsHolder(UUID uuid) {
        this.uuid = uuid;
    }

    public DataFile getDataFile(UUID p){
        return BukkitDataFile.parseFromGeneralPath(CruxFolder.file(Crux.getMainPlugin(), "data/usurvive/player/settings/" + p + ".json"));
    }

    public DataFile getOrCreateDataFile(UUID p){
        return BukkitDataFile.parseFromGeneralPath(CruxFolder.file(Crux.getMainPlugin(), "data/usurvive/player/settings/" + p + ".json"), true);
    }

    @Override
    public void save() {
        DataFile file = getOrCreateDataFile(uuid);
        if(file == null) return;
        file.serialize("settings", settings);
        file.save();
    }

    @Override
    public void load() {
        DataFile file = getDataFile(uuid);
        if(file == null) return;
        Map<PlayerSetting, PlayerSettingSelection> map = file.deserialize(
            "settings", new TypeToken<Map<PlayerSetting, PlayerSettingSelection>>(){}.getType()
        );
        file.close();
        if(map != null){
            settings.putAll(map);
        }
    }


    @Override
    public PlayerSettingSelection getSetting(@NotNull PlayerSetting setting) {
        return settings.getOrDefault(setting, setting.defaultSelection());
    }

    @Override
    public boolean hasSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection) {
        return selection.compare(getSetting(setting));
    }

    @Override
    public void setSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection) {
        if(selection.equals(getSetting(setting))) return;
        if(setting.defaultSelection().equals(selection)){
            settings.remove(setting);
        }else settings.put(setting, selection);
    }

    @NotNull
    @Override
    public Collection<PlayerSetting> getAllAvailableSettings() {
        return USurviveRegistries.PLAYER_SETTING.values();
    }

    @NotNull
    @Override
    public Map<PlayerSetting, PlayerSettingSelection> getSettings() {
        return settings;
    }
}
