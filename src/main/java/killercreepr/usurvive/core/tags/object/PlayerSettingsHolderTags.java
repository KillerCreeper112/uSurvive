package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringListResolver;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxString;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.core.data.entity.SimplePlayerSettingsHolder;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class PlayerSettingsHolderTags implements ObjectTag<PlayerSettingsHolder> {
    @Override
    public @NotNull Class<PlayerSettingsHolder> getObjectType() {
        return PlayerSettingsHolder.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("player_settings_holder_");
    }

    @Nullable
    @Override
    public TagContainer<StringListResolver> requestStringLists(@NotNull PlayerSettingsHolder object, @NotNull TagParser tags) {
        return TagContainer.stringList(tags)
            ;
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull PlayerSettingsHolder object, @NotNull TagParser tags) {
        PlayerSettingsHolder cache;
        if(object instanceof SimplePlayerSettingsHolder h){
            if(h.getCache() != null) cache = h.getCache();
            else cache = object;
        }else cache = object;

        return TagContainer.string(tags)
            .add(Tag.string("cache_changes", (args, ctx) ->{
                if(cache == object) return "0";
                int changes = 0;
                for (Map.Entry<PlayerSetting, PlayerSettingSelection> entry : cache.getSettings().entrySet()) {
                    if(!object.hasSetting(entry.getKey(), entry.getValue())) changes++;
                }
                return changes + "";
            }))
            .add(Tag.string("has_setting", (args, ctx) ->{
                boolean includeCache = args.has(2) && CruxString.parseBoolean(args.get(1));
                PlayerSettingsHolder check = includeCache ? cache : object;
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                key = Crux.key(args.get(1));
                PlayerSettingSelection selection = USurviveRegistries.PLAYER_SETTING_SELECTION.get(key);
                Objects.requireNonNull(selection, "No PlayerSettingSelection found! " + key);
                return check.hasSetting(setting, selection) + "";
            }))
            .add(Tag.string("setting", (args, ctx) ->{
                boolean includeCache = args.has(1) && CruxString.parseBoolean(args.get(1));
                PlayerSettingsHolder check = includeCache ? cache : object;
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return check.getSetting(setting).displayName();
            }))
            .add(Tag.string("setting_key", (args, ctx) ->{
                boolean includeCache = args.has(1) && CruxString.parseBoolean(args.get(1));
                PlayerSettingsHolder check = includeCache ? cache : object;
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return check.getSetting(setting).key().asString();
            }))
            .add(Tag.string("setting_description", (args, ctx) ->{
                boolean includeCache = args.has(1) && CruxString.parseBoolean(args.get(1));
                PlayerSettingsHolder check = includeCache ? cache : object;
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return setting.getDescription(check.getSetting(setting)) + "";
            }))
            .add(Tag.string("next_selection", (args, ctx) ->{
                boolean includeCache = args.has(1) && CruxString.parseBoolean(args.get(1));
                PlayerSettingsHolder check = includeCache ? cache : object;
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return setting.nextSelection(check.getSetting(setting)).key().asString();
            }))
            .add(Tag.string("next_selection_name", (args, ctx) ->{
                boolean includeCache = args.has(1) && CruxString.parseBoolean(args.get(1));
                PlayerSettingsHolder check = includeCache ? cache : object;
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return setting.nextSelection(check.getSetting(setting)).displayName();
            }))
            .add(Tag.string("previous_selection", (args, ctx) ->{
                boolean includeCache = args.has(1) && CruxString.parseBoolean(args.get(1));
                PlayerSettingsHolder check = includeCache ? cache : object;
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return setting.previousSelection(check.getSetting(setting)).key().asString();
            }))
            .add(Tag.string("previous_selection_name", (args, ctx) ->{
                boolean includeCache = args.has(1) && CruxString.parseBoolean(args.get(1));
                PlayerSettingsHolder check = includeCache ? cache : object;
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return setting.previousSelection(check.getSetting(setting)).displayName();
            }))
            ;
    }
}
