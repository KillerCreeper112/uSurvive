package killercreepr.usurvive.core.data.entity;

import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerDataHolder;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.core.data.SimplePlayerSettingsCache;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class MemoryPlayerSettingsHolder extends PlayerDataHolder implements PlayerSettingsHolder, Loadable {
    public static final Key KEY = Crux.key("player_settings");
    public MemoryPlayerSettingsHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
        data = new DataPlayerSettingsHolder(parent.getUUID());
        load();
    }

    public MemoryPlayerSettingsHolder(@NotNull PlayerMemory parent) {
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
        data.getSettings().forEach(holder::setSetting);
        return holder;
    }

    protected final @NotNull DataPlayerSettingsHolder data;

    public PlayerSettingSelection getSetting(@NotNull PlayerSetting setting){
        return data.getSetting(setting);
    }

    public boolean hasSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection){
        return data.hasSetting(setting, selection);
    }

    @Override
    public void setSetting(@NotNull PlayerSetting setting, @NotNull PlayerSettingSelection selection) {
        data.setSetting(setting, selection);

        HumanEntity found = getPlayer();
        if(found != null){
            setting.onSettingChanged(found, getSetting(setting));
        }
    }

    @NotNull
    @Override
    public Collection<PlayerSetting> getAllAvailableSettings() {
        return data.getAllAvailableSettings();
    }

    @NotNull
    @Override
    public Map<PlayerSetting, PlayerSettingSelection> getSettings() {
        return data.getSettings();
    }

    @Override
    protected void removingFromMemory(@Nullable Entity e) {
        super.removingFromMemory(e);
        save();
    }

    @Override
    public void save() {
        data.save();
    }

    @Override
    public void load() {
        data.load();
    }
}
