package killercreepr.usurvive.core.entity.player.settings;

import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class SimplePlayerSettingSelection implements PlayerSettingSelection {
    protected final @NotNull Key key;
    public SimplePlayerSettingSelection(@NotNull Key key) {
        this.key = key;
    }

    @Override
    public @NotNull Key key() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplePlayerSettingSelection that = (SimplePlayerSettingSelection) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "SimplePlayerSettingSelection{key=" + key + "}";
    }
}
