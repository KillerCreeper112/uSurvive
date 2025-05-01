package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.core.entity.memory.SleeperHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SleeperHolderTags implements ObjectTag<SleeperHolder> {
    @Override
    public @NotNull Class<SleeperHolder> getObjectType() {
        return SleeperHolder.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("sleeper_holder_");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull SleeperHolder object, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("sleeping_ticks", (args, ctx) -> object.getTicksSleeping() + ""))
            .add(Tag.string("time_passed", (args, ctx) -> object.getTimePassed() + ""))
            ;
    }
}
