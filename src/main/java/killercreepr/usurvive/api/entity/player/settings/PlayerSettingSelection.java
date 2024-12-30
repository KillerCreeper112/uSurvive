package killercreepr.usurvive.api.entity.player.settings;

import killercreepr.crux.api.data.CruxKeyed;
import killercreepr.crux.core.util.CruxString;
import killercreepr.usurvive.core.entity.player.settings.SimplePlayerSettingSelection;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public interface PlayerSettingSelection extends CruxKeyed {
    static PlayerSettingSelection playerSettingSelection(@NotNull Key key){
        return new SimplePlayerSettingSelection(key);
    }

    default @NotNull String displayName(){
        return CruxString.toTitleCase(key().value());
    }
}
