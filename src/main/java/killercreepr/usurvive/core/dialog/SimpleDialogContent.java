package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogLine;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleDialogContent implements DialogContent {
    protected final List<DialogLine> dialog;
    protected final List<DialogAction> actions;
    protected final DataExchange info;

    public SimpleDialogContent(List<DialogLine> dialog, List<DialogAction> actions, DataExchange info) {
        this.dialog = dialog;
        this.actions = actions;
        this.info = info;
    }

    @Override
    public List<DialogLine> dialog() {
        return dialog;
    }

    @Override
    public List<DialogAction> actions() {
        return actions;
    }

    @Override
    public @NotNull DataExchange info() {
        return info;
    }
}
