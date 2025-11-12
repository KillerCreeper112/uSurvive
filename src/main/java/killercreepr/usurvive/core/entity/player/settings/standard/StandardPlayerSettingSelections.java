package killercreepr.usurvive.core.entity.player.settings.standard;

import killercreepr.crux.core.Crux;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.core.registries.USurviveRegistries;

public class StandardPlayerSettingSelections {
    public static final PlayerSettingSelection ENABLED = register(PlayerSettingSelection.playerSettingSelection(Crux.key("enabled")));
    public static final PlayerSettingSelection DISABLED = register(PlayerSettingSelection.playerSettingSelection(Crux.key("disabled")));

    public static final PlayerSettingSelection LOCKED = register(PlayerSettingSelection.playerSettingSelection(Crux.key("locked")));
    public static final PlayerSettingSelection UNLOCKED = register(PlayerSettingSelection.playerSettingSelection(Crux.key("unlocked")));
    public static final PlayerSettingSelection WHOLE = register(PlayerSettingSelection.playerSettingSelection(Crux.key("whole")));
    public static final PlayerSettingSelection HALF = register(PlayerSettingSelection.playerSettingSelection(Crux.key("half")));
    public static final PlayerSettingSelection TINY = register(PlayerSettingSelection.playerSettingSelection(Crux.key("tiny")));
    public static final PlayerSettingSelection SHORT = register(PlayerSettingSelection.playerSettingSelection(Crux.key("short")));
    public static final PlayerSettingSelection NORMAL = register(PlayerSettingSelection.playerSettingSelection(Crux.key("normal")));
    public static final PlayerSettingSelection FAR = register(PlayerSettingSelection.playerSettingSelection(Crux.key("far")));
    public static final PlayerSettingSelection FRIENDS_ONLY = register(PlayerSettingSelection.playerSettingSelection(Crux.key("friends_only")));
    public static final PlayerSettingSelection ALL = register(PlayerSettingSelection.playerSettingSelection(Crux.key("all")));
    public static final PlayerSettingSelection NONE = register(PlayerSettingSelection.playerSettingSelection(Crux.key("none")));
    public static final PlayerSettingSelection TITLE = register(PlayerSettingSelection.playerSettingSelection(Crux.key("title")));
    public static final PlayerSettingSelection ACTION_BAR = register(PlayerSettingSelection.playerSettingSelection(Crux.key("action_bar")));
    public static final PlayerSettingSelection SOUND = register(PlayerSettingSelection.playerSettingSelection(Crux.key("sound")));
    public static final PlayerSettingSelection CHAT = register(PlayerSettingSelection.playerSettingSelection(Crux.key("chat")));
    public static final PlayerSettingSelection STRONG = register(PlayerSettingSelection.playerSettingSelection(Crux.key("strong")));
    public static final PlayerSettingSelection WEAK = register(PlayerSettingSelection.playerSettingSelection(Crux.key("weak")));
    public static final PlayerSettingSelection SIMPLE = register(PlayerSettingSelection.playerSettingSelection(Crux.key("simple")));
    public static final PlayerSettingSelection ADVANCED = register(PlayerSettingSelection.playerSettingSelection(Crux.key("advanced")));

    public static final PlayerSettingSelection SLOW = register(PlayerSettingSelection.playerSettingSelection(Crux.key("slow")));
    public static final PlayerSettingSelection FAST = register(PlayerSettingSelection.playerSettingSelection(Crux.key("fast")));
    public static final PlayerSettingSelection SUPER_FAST = register(PlayerSettingSelection.playerSettingSelection(Crux.key("super_fast")));
    public static final PlayerSettingSelection INSTANT = register(PlayerSettingSelection.playerSettingSelection(Crux.key("instant")));

    public static PlayerSettingSelection register(PlayerSettingSelection s){
        return USurviveRegistries.PLAYER_SETTING_SELECTION.register(s);
    }
}
