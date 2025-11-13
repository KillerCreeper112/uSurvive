package killercreepr.usurvive.core.dialog.action;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import org.bukkit.entity.Player;

import java.util.List;

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
        player.getDialog().sendInfo(cmds);
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
