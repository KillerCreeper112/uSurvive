package killercreepr.usurvive.core.dialog.action;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import killercreepr.usurvive.api.event.DialogSendContentInfoEvent;

public class SendContentInfoDialogAction implements DialogAction {
    protected final DataExchange start;
    protected final DataExchange end;
    protected final DataExchange speak;

    public SendContentInfoDialogAction(DataExchange start, DataExchange end, DataExchange speak) {
        this.start = start;
        this.end = end;
        this.speak = speak;
    }

    @Override
    public void onStart(DialogPlayer player) {
        performCommand(start, player);
    }

    public void performCommand(DataExchange cmds, DialogPlayer player){
        if(cmds == null) return;
        var dialog = player.getDialog();
        dialog.onSendInfo(cmds);
        new DialogSendContentInfoEvent(player, dialog, cmds).callEvent();
    }

    @Override
    public void onEnd(DialogPlayer player) {
        performCommand(end, player);

    }

    @Override
    public void onSpeak(DialogPlayer player) {
        performCommand(speak, player);

    }
}
