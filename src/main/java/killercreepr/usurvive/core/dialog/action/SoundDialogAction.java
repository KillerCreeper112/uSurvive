package killercreepr.usurvive.core.dialog.action;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.Crux;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogPlayer;

public class SoundDialogAction implements DialogAction {
    protected final CreateSound start;
    protected final CreateSound end;
    protected final CreateSound speak;

    public SoundDialogAction(CreateSound start, CreateSound end, CreateSound speak) {
        this.start = start;
        this.end = end;
        this.speak = speak;
    }

    @Override
    public void onStart(DialogPlayer player) {
        if(start != null){
            player.audience().forEach(start::playFor);
        }
    }

    @Override
    public void onEnd(DialogPlayer player) {
        if(end != null){
            player.audience().forEach(end::playFor);
        }

    }

    @Override
    public void onSpeak(DialogPlayer player) {
        if(speak != null){
            player.audience().forEach(speak::playFor);
        }

    }
}
