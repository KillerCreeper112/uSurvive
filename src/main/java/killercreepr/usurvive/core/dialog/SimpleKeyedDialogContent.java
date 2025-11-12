package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.api.dialog.KeyedDialogContent;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleKeyedDialogContent extends SimpleDialogContent implements KeyedDialogContent{
    public SimpleKeyedDialogContent(List<DialogLine> dialog, List<DialogAction> actions, DataExchange info) {
        super(dialog, actions, info);
    }

    @Override
    public @NotNull Key key() {
        return info.getOrThrow("key", Key.class);
    }
}
