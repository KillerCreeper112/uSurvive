package killercreepr.usurvive.core.entity.player.settings.standard;

import killercreepr.crux.core.Crux;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.core.entity.player.settings.SimplePlayerSetting;
import killercreepr.usurvive.core.registries.USurviveRegistries;

import java.util.List;
import java.util.Map;

public class StandardPlayerSettings {
    public static void register(){}
    public static final PlayerSetting QUICK_ACCESS_ITEM = register(new QuickAccessSetting(
        Crux.key("quick_access_item"), StandardPlayerSettingSelections.LOCKED, List.of(
            StandardPlayerSettingSelections.LOCKED, StandardPlayerSettingSelections.UNLOCKED, StandardPlayerSettingSelections.DISABLED
        ),
        Map.of(
            StandardPlayerSettingSelections.LOCKED, "Locks the quick access item in your inventory",
            StandardPlayerSettingSelections.UNLOCKED, "Unlocks the quick access item in your inventory. This allows you to move it around in anywhere in your inventory",
            StandardPlayerSettingSelections.DISABLED, "Disables the quick access item entirely. You can still use <gray>/menu</gray> to open up the quick access menu"
        )
    ));

    public static final PlayerSetting TPA_REQUESTS = register(new SimplePlayerSetting(
        Crux.key("tpa_requests"), StandardPlayerSettingSelections.ENABLED,
        List.of(
            StandardPlayerSettingSelections.ENABLED,
            StandardPlayerSettingSelections.FRIENDS_ONLY,
            StandardPlayerSettingSelections.DISABLED
        ),
        Map.of(
            StandardPlayerSettingSelections.ENABLED, "You will receive teleport requests from all players",
            StandardPlayerSettingSelections.FRIENDS_ONLY, "You will only receive teleport requests from your friends",
            StandardPlayerSettingSelections.DISABLED, "You will not receive any teleport requests"
        )
    ));

    public static final PlayerSetting TPA_HERE_REQUESTS = register(new SimplePlayerSetting(
        Crux.key("tpa_here_requests"), StandardPlayerSettingSelections.ENABLED,
        List.of(
            StandardPlayerSettingSelections.ENABLED,
            StandardPlayerSettingSelections.FRIENDS_ONLY,
            StandardPlayerSettingSelections.DISABLED
        ),
        Map.of(
            StandardPlayerSettingSelections.ENABLED, "You will receive teleport here requests from all players",
            StandardPlayerSettingSelections.FRIENDS_ONLY, "You will only receive teleport here requests from your friends",
            StandardPlayerSettingSelections.DISABLED, "You will not receive any teleport here requests"
        )
    ));

    public static final PlayerSetting QUICK_RECIPE_BOOK = register(new SimplePlayerSetting(
        Crux.key("quick_recipe_book"), StandardPlayerSettingSelections.ENABLED,
        List.of(
            StandardPlayerSettingSelections.ENABLED,
            StandardPlayerSettingSelections.DISABLED
        ),
        Map.of(
            StandardPlayerSettingSelections.ENABLED, "When you have a crafting table menu opened, swap your hands (default key is F) to quickly open the custom recipes book",
            StandardPlayerSettingSelections.DISABLED, "Swapping your hands while you have a crafting table menu opened will no longer open the custom recipes book. You can still access it via your quick access menu or with /recipes"
        )
    ));

    public static final PlayerSetting GUIDE = register(new SimplePlayerSetting(
        Crux.key("guide"), StandardPlayerSettingSelections.ENABLED,
        List.of(
            StandardPlayerSettingSelections.ENABLED,
            StandardPlayerSettingSelections.DISABLED
        ),
        Map.of(
            StandardPlayerSettingSelections.ENABLED, "You will have a general guide to help you get started on the server at your own pace",
            StandardPlayerSettingSelections.DISABLED, "You will not have a general guide. This option is not recommended for new players"
        )
    ));

    public static final PlayerSetting SPAWN_GRAVESTONE = register(new SimplePlayerSetting(
        Crux.key("spawn_gravestone"), StandardPlayerSettingSelections.ENABLED,
        List.of(
            StandardPlayerSettingSelections.ENABLED,
            StandardPlayerSettingSelections.DISABLED
        ),
        Map.of(
            StandardPlayerSettingSelections.ENABLED, "Gravestone will spawn upon death",
            StandardPlayerSettingSelections.DISABLED, "Gravestone will not spawn upon death"
        )
    ));

    public static final PlayerSetting CUSTOM_DEATH_RESPAWN = register(new SimplePlayerSetting(
        Crux.key("custom_death_respawn"), StandardPlayerSettingSelections.ENABLED,
        List.of(
            StandardPlayerSettingSelections.ENABLED,
            StandardPlayerSettingSelections.DISABLED
        ),
        Map.of(
            StandardPlayerSettingSelections.ENABLED, "",
            StandardPlayerSettingSelections.DISABLED, ""
        )
    ));

    public static final PlayerSetting DIALOG_SPEED = register(new SimplePlayerSetting(
        Crux.key("dialog_speed"), StandardPlayerSettingSelections.FAST,
        List.of(
            StandardPlayerSettingSelections.SLOW,
            StandardPlayerSettingSelections.NORMAL,
            StandardPlayerSettingSelections.FAST,
            StandardPlayerSettingSelections.SUPER_FAST
        ),
        Map.of(
            StandardPlayerSettingSelections.SLOW, "Slow dialog speed",
            StandardPlayerSettingSelections.NORMAL, "Normal dialog speed",
            StandardPlayerSettingSelections.FAST, "Fast dialog speed",
            StandardPlayerSettingSelections.SUPER_FAST, "Super fast dialog speed"
        )
    ));

    public static final PlayerSetting DIALOG_PAUSE_TIME = register(new SimplePlayerSetting(
        Crux.key("dialog_pause_time"), StandardPlayerSettingSelections.FAST,
        List.of(
            StandardPlayerSettingSelections.SLOW,
            StandardPlayerSettingSelections.NORMAL,
            StandardPlayerSettingSelections.FAST,
            StandardPlayerSettingSelections.SUPER_FAST
        ),
        Map.of(
            StandardPlayerSettingSelections.SLOW, "Slow dialog pause",
            StandardPlayerSettingSelections.NORMAL, "Normal dialog pause",
            StandardPlayerSettingSelections.FAST, "Fast dialog pause",
            StandardPlayerSettingSelections.SUPER_FAST, "Super fast dialog pause"
        )
    ));

    public static final PlayerSetting TIPS = register(new SimplePlayerSetting(
        Crux.key("tips"), StandardPlayerSettingSelections.ENABLED,
        List.of(
            StandardPlayerSettingSelections.ENABLED,
            StandardPlayerSettingSelections.OCCASIONAL,
            StandardPlayerSettingSelections.DEATH_ONLY,
            StandardPlayerSettingSelections.DISABLED
        ),
        Map.of(
            StandardPlayerSettingSelections.ENABLED, "You will receive tip messages every once in a while",
            StandardPlayerSettingSelections.OCCASIONAL, "Tip messages will appear less frequent",
            StandardPlayerSettingSelections.DEATH_ONLY, "Tip messages will only appear after you die",
            StandardPlayerSettingSelections.DISABLED, "You will not receive any tip messages"
        )
    ));

    public static PlayerSetting register(PlayerSetting s){
        return USurviveRegistries.PLAYER_SETTING.register(s);
    }
}
