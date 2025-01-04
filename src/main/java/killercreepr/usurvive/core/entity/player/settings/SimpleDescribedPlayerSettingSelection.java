package killercreepr.usurvive.core.entity.player.settings;

import killercreepr.usurvive.api.entity.player.settings.DescribedPlayerSettingSelection;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class SimpleDescribedPlayerSettingSelection extends SimplePlayerSettingSelection implements DescribedPlayerSettingSelection {
    protected final String description;
    public SimpleDescribedPlayerSettingSelection(@NotNull Key key, String description) {
        super(key);
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
