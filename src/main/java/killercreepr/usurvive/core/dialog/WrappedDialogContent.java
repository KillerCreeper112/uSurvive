package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogLine;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WrappedDialogContent implements DialogContent {
    protected final DialogContent child;

    public WrappedDialogContent(DialogContent child) {
        this.child = child;
    }

    public DialogContent getChild() {
        return child;
    }

    @Override
    public List<DialogLine> dialog() {
        return child.dialog();
    }

    @Override
    public List<DialogAction> actions() {
        return child.actions();
    }

    @Override
    public int priority() {
        return child.priority();
    }

    @Override
    public @NotNull DataExchange info() {
        return child.info();
    }
}
