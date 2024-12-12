package killercreepr.usurvive.api.entity.player.settings;

import killercreepr.crux.api.data.CruxKeyed;
import killercreepr.usurvive.core.entity.player.settings.SimplePlayerSetting;
import killercreepr.usurvive.core.entity.player.settings.StandardPlayerSettingSelections;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public interface PlayerSetting extends CruxKeyed {
    @NotNull Collection<PlayerSettingSelection> validSelections();
    @NotNull PlayerSettingSelection defaultSelection();

    default boolean isValidSelection(@NotNull PlayerSettingSelection selection){
        return validSelections().contains(selection);
    }

    static @NotNull PlayerSetting simpleToggle(@NotNull Key id){
        return simpleToggle(id, true);
    }

    static @NotNull PlayerSetting simpleToggle(@NotNull Key id, boolean enabled){
        return playerSetting(id,
            enabled ? StandardPlayerSettingSelections.ENABLED : StandardPlayerSettingSelections.DISABLED,
            Set.of(StandardPlayerSettingSelections.ENABLED, StandardPlayerSettingSelections.DISABLED));
    }

    static @NotNull PlayerSetting playerSetting(@NotNull Key id, @NotNull PlayerSettingSelection defaultSelection, @NotNull Collection<PlayerSettingSelection> validSelections){
        return new SimplePlayerSetting(id, defaultSelection, validSelections);
    }
}
