package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogLine;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleDialogLine implements DialogLine {
    protected final String line;
    protected final List<DialogAction> actions;
    protected final DataExchange info;

    public SimpleDialogLine(String line, List<DialogAction> actions, DataExchange info) {
        this.line = line;
        this.actions = actions;
        this.info = info;
    }

    @Override
    public String line() {
        return line;
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
