package killercreepr.usurvive.core.dialog.action;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogPlayer;

public class SetWaitingDialogAction implements DialogAction {
    protected final NumberProvider start;
    protected final NumberProvider end;
    protected final NumberProvider speak;

    public SetWaitingDialogAction(NumberProvider start, NumberProvider end, NumberProvider speak) {
        this.start = start;
        this.end = end;
        this.speak = speak;
    }

    @Override
    public void onStart(DialogPlayer player) {
        if(start != null) player.setDialogWaiting(start.value().intValue());
    }

    @Override
    public void onEnd(DialogPlayer player) {
        if(end != null) player.setDialogWaiting(end.value().intValue());

    }

    @Override
    public void onSpeak(DialogPlayer player) {
        if(speak != null) player.setDialogWaiting(speak.value().intValue());

    }
}
