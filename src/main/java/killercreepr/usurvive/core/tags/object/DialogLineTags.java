package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.SimpleObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.api.dialog.DialogLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DialogLineTags implements SimpleObjectTag<DialogLine> {
    @NotNull
    @Override
    public Class<DialogLine> getObjectType() {
        return DialogLine.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("dialog_line_");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull DialogLine line, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("line", (args, ctx) ->{
                return line.line();
            }))
            ;
    }
}
