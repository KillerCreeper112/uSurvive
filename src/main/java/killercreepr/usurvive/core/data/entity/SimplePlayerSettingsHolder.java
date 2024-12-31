package killercreepr.usurvive.core.data.entity;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerDataHolder;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.file.CruxJson;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.core.data.SimplePlayerSettingsCache;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimplePlayerSettingsHolder extends PlayerDataHolder implements PlayerSettingsHolder, Loadable {
    public static final Key KEY = Crux.key("player_settings");
    public SimplePlayerSettingsHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
        load();
    }

    public SimplePlayerSettingsHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }
    protected PlayerSettingsHolder cache;

    public PlayerSettingsHolder getCache() {
        return cache;
    }

    public void setCache(PlayerSettingsHolder cache) {
        this.cache = cache;
    }

    public PlayerSettingsHolder createCache(){
        PlayerSettingsHolder holder = new SimplePlayerSettingsCache();
        settings.forEach(holder::setSetting);
        return holder;
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

    @Override
    protected void removingFromMemory(@Nullable Entity e) {
        super.removingFromMemory(e);
        save();
    }

    public DataFile getDataFile(UUID p){
        File f = CruxFolder.file(Crux.getMainPlugin(), "data/usurvive/player/settings/" + p + ".json");
        if(BukkitDataFile.isSupported(f)) return BukkitDataFile.parse(f);
        return null;
    }

    public DataFile getOrCreateDataFile(UUID p){
        File f = CruxFolder.file(Crux.getMainPlugin(), "data/usurvive/player/settings/" + p + ".json");
        if(!f.exists()){
            CruxJson cfg = new CruxJson(f);
            cfg.reloadIfNeeded();
            cfg.close();
        }
        if(BukkitDataFile.isSupported(f)) return BukkitDataFile.parse(f);
        return null;
    }

    @Override
    public void save() {
        DataFile file = getOrCreateDataFile(parent.getUUID());
        if(file == null) return;
        file.serialize("settings", settings);
        file.save();
    }

    @Override
    public void load() {
        DataFile file = getDataFile(parent.getUUID());
        if(file == null) return;
        Map<PlayerSetting, PlayerSettingSelection> map = file.deserialize(
            "settings", new TypeToken<Map<PlayerSetting, PlayerSettingSelection>>(){}.getType()
        );
        file.close();
        if(map != null){
            settings.putAll(map);
        }
    }
}
