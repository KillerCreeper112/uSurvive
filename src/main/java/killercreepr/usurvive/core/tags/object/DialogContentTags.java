package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.SimpleObjectTag;
import killercreepr.usurvive.api.dialog.DialogContent;
import org.jetbrains.annotations.NotNull;

public class DialogContentTags implements SimpleObjectTag<DialogContent> {
    @NotNull
    @Override
    public Class<DialogContent> getObjectType() {
        return DialogContent.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("dialog_content_");
    }
}
