package killercreepr.usurvive.core.entity.player.settings;

import killercreepr.crux.api.data.Holder;
import killercreepr.usurvive.api.entity.player.settings.DescribedPlayerSettingSelection;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedPlayerSetting implements PlayerSetting {
    protected final @NotNull Key key;
    protected final @NotNull PlayerSettingSelection defaultSelection;
    protected final @NotNull Holder<List<PlayerSettingSelection>> validSelections;

    public CachedPlayerSetting(@NotNull Key key, @NotNull PlayerSettingSelection defaultSelection, @NotNull Holder<List<PlayerSettingSelection>> validSelections) {
        this.key = key;
        this.defaultSelection = defaultSelection;
        this.validSelections = validSelections;
    }
    protected List<PlayerSettingSelection> cacheValid;
    protected Map<PlayerSettingSelection, String> descriptions;

    @NotNull
    @Override
    public List<PlayerSettingSelection> validSelections() {
        if(cacheValid != null) return cacheValid;
        cacheValid = validSelections.value();
        return cacheValid;
    }

    @NotNull
    @Override
    public PlayerSettingSelection defaultSelection() {
        return defaultSelection;
    }

    @Nullable
    @Override
    public String getDescription(@NotNull PlayerSettingSelection selection) {
        if(descriptions != null) return descriptions.get(selection);
        descriptions = new HashMap<>();

        validSelections().forEach(sel ->{
            if(sel instanceof DescribedPlayerSettingSelection d) descriptions.put(d, d.getDescription());
        });

        return descriptions.get(selection);
    }

    @Override
    public @NotNull Key key() {
        return key;
    }
}
