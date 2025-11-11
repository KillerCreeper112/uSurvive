package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.KeyedDialoger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleDialoger implements KeyedDialoger {
    protected final Key key;
    protected final List<DialogContent> dialogContent;
    protected final List<DialogAction> actions;
    protected final DataExchange info;

    public SimpleDialoger(Key key, List<DialogContent> dialogContent, List<DialogAction> actions, DataExchange info) {
        this.key = key;
        this.dialogContent = dialogContent;
        this.actions = actions;
        this.info = info;
    }

    @Override
    public List<DialogContent> dialogContent() {
        return dialogContent;
    }

    @Override
    public List<DialogAction> actions() {
        return actions;
    }

    @NotNull
    @Override
    public Key key() {
        return key;
    }

    @Override
    public @NotNull DataExchange info() {
        return info;
    }
}
