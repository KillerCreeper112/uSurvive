package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerSettingSelectionTags implements ObjectTag<PlayerSettingSelection> {
    @NotNull
    @Override
    public Class<PlayerSettingSelection> getObjectType() {
        return PlayerSettingSelection.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("player_setting_selection_");
    };

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull PlayerSettingSelection object, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("name", (args, ctx) -> object.displayName()))
            .add(Tag.string("key", (args, ctx) -> object.key().asString()))
            ;
    }
}
