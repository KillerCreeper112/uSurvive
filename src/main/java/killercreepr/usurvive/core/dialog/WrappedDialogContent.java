package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.api.dialog.DialogPlayer;
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
    public boolean canPlay(DialogPlayer player) {
        return child.canPlay(player);
    }

    @Override
    public boolean hasCustomCanPlayLogic() {
        return child.hasCustomCanPlayLogic();
    }

    @Override
    public @NotNull DataExchange info() {
        return child.info();
    }

    @Override
    public void onSendInfo(DataExchange info) {
        child.onSendInfo(info);
    }

    @Override
    public void onStart(DialogPlayer player) {
        child.onStart(player);
    }

    @Override
    public void onPause(DialogPlayer player) {
        child.onPause(player);
    }

    @Override
    public void onDialogEnd(DialogPlayer player) {
        child.onDialogEnd(player);
    }

    @Override
    public void onEnd(DialogPlayer player) {
        child.onEnd(player);
    }
}
