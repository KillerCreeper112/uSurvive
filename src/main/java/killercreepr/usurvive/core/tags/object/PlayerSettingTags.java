package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PlayerSettingTags implements ObjectTag<PlayerSetting> {
    @NotNull
    @Override
    public Class<PlayerSetting> getObjectType() {
        return PlayerSetting.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("player_setting_");
    };

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull PlayerSetting object, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("description", (args, ctx) ->{
                Key key = Crux.key(args.get(0));
                PlayerSettingSelection selection = USurviveRegistries.PLAYER_SETTING_SELECTION.get(key);
                Objects.requireNonNull(selection, "No PlayerSettingSelection found! " + key);
                return object.getDescription(selection) + "";
            }))
            .add(Tag.string("key", (args, ctx) -> object.key().asString()))
            ;
    }
}
