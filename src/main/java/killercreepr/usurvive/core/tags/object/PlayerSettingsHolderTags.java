package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringListResolver;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return TagContainer.string(tags)
            .add(Tag.string("has_setting", (args, ctx) ->{
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                key = Crux.key(args.get(1));
                PlayerSettingSelection selection = USurviveRegistries.PLAYER_SETTING_SELECTION.get(key);
                Objects.requireNonNull(selection, "No PlayerSettingSelection found! " + key);
                return object.hasSetting(setting, selection) + "";
            }))
            .add(Tag.string("setting", (args, ctx) ->{
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return object.getSetting(setting).displayName();
            }))
            .add(Tag.string("setting_key", (args, ctx) ->{
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return object.getSetting(setting).key().asString();
            }))
            .add(Tag.string("setting_description", (args, ctx) ->{
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return setting.getDescription(object.getSetting(setting)) + "";
            }))
            .add(Tag.string("next_selection", (args, ctx) ->{
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return setting.nextSelection(object.getSetting(setting)).key().asString();
            }))
            .add(Tag.string("next_selection_name", (args, ctx) ->{
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return setting.nextSelection(object.getSetting(setting)).displayName();
            }))
            .add(Tag.string("previous_selection", (args, ctx) ->{
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return setting.previousSelection(object.getSetting(setting)).key().asString();
            }))
            .add(Tag.string("previous_selection_name", (args, ctx) ->{
                Key key = Crux.key(args.get(0));
                PlayerSetting setting = USurviveRegistries.PLAYER_SETTING.get(key);
                Objects.requireNonNull(setting, "No PlayerSetting found! " + key);
                return setting.previousSelection(object.getSetting(setting)).displayName();
            }))
            ;
    }
}
