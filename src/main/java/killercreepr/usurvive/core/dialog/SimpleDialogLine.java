package killercreepr.usurvive.core.dialog;

import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogLine;

import java.util.List;

public class SimpleDialogLine implements DialogLine {
    protected final String line;
    protected final List<DialogAction> actions;

    public SimpleDialogLine(String line, List<DialogAction> actions) {
        this.line = line;
        this.actions = actions;
    }

    @Override
    public String line() {
        return line;
    }

    @Override
    public List<DialogAction> actions() {
        return actions;
    }
}
