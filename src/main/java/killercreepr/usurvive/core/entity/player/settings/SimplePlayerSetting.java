package killercreepr.usurvive.core.entity.player.settings;

import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class SimplePlayerSetting implements PlayerSetting {
    protected final @NotNull Key key;
    protected final @NotNull PlayerSettingSelection defaultSelection;
    protected final @NotNull List<PlayerSettingSelection> validSelections;
    protected final @NotNull Map<PlayerSettingSelection, String> descriptions;
    public SimplePlayerSetting(@NotNull Key key, @NotNull PlayerSettingSelection defaultSelection, @NotNull List<PlayerSettingSelection> validSelections, @NotNull Map<PlayerSettingSelection, String> descriptions) {
        this.key = key;
        this.defaultSelection = defaultSelection;
        this.validSelections = validSelections;
        this.descriptions = descriptions;
    }

    @NotNull
    @Override
    public String getDescription(@NotNull PlayerSettingSelection selection) {
        return descriptions.get(selection);
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
