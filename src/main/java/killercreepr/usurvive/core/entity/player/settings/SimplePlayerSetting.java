package killercreepr.usurvive.core.entity.player.settings;

import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimplePlayerSetting implements PlayerSetting {
    protected final @NotNull Key key;
    protected final @NotNull PlayerSettingSelection defaultSelection;
    protected final @NotNull List<PlayerSettingSelection> validSelections;
    public SimplePlayerSetting(@NotNull Key key, @NotNull PlayerSettingSelection defaultSelection, @NotNull List<PlayerSettingSelection> validSelections) {
        this.key = key;
        this.defaultSelection = defaultSelection;
        this.validSelections = validSelections;
    }

    @NotNull
    @Override
    public List<PlayerSettingSelection> validSelections() {
        return validSelections;
    }

    @NotNull
    @Override
    public PlayerSettingSelection defaultSelection() {
        return defaultSelection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplePlayerSetting that = (SimplePlayerSetting) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "SimplePlayerSetting{key=" + key + "}";
    }

    @Override
    public @NotNull Key key() {
        return key;
    }
}
