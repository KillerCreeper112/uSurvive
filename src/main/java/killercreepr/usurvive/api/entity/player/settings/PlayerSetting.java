package killercreepr.usurvive.api.entity.player.settings;

import killercreepr.crux.api.data.CruxKeyed;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.usurvive.core.entity.player.settings.SimplePlayerSetting;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettingSelections;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PlayerSetting extends CruxKeyed {
    @NotNull List<PlayerSettingSelection> validSelections();
    @NotNull PlayerSettingSelection defaultSelection();

    default void onSettingChanged(@NotNull HumanEntity e, @NotNull PlayerSettingSelection selection){

    }

    default @NotNull PlayerSettingSelection nextSelection(@NotNull PlayerSettingSelection current){
        List<PlayerSettingSelection> list = validSelections();
        int currentIndex = list.indexOf(current);
        return list.get(CruxMath.wrap(currentIndex + 1, 0, list.size()-1));
    }
    default @NotNull PlayerSettingSelection previousSelection(@NotNull PlayerSettingSelection current){
        List<PlayerSettingSelection> list = validSelections();
        int currentIndex = list.indexOf(current);
        return list.get(CruxMath.wrap(currentIndex - 1, 0, list.size()-1));
    }

    default boolean isValidSelection(@NotNull PlayerSettingSelection selection){
        return validSelections().contains(selection);
    }

    static @NotNull PlayerSetting simpleToggle(@NotNull Key id){
        return simpleToggle(id, true);
    }

    static @NotNull PlayerSetting simpleToggle(@NotNull Key id, boolean enabled){
        return playerSetting(id,
            enabled ? StandardPlayerSettingSelections.ENABLED : StandardPlayerSettingSelections.DISABLED,
            List.of(StandardPlayerSettingSelections.ENABLED, StandardPlayerSettingSelections.DISABLED));
    }

    static @NotNull PlayerSetting playerSetting(@NotNull Key id, @NotNull PlayerSettingSelection defaultSelection, @NotNull List<PlayerSettingSelection> validSelections){
        return new SimplePlayerSetting(id, defaultSelection, validSelections);
    }
}
