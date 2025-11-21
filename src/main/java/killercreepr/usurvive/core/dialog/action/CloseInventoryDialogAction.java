package killercreepr.usurvive.core.dialog.action;

import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import org.bukkit.entity.HumanEntity;

public class CloseInventoryDialogAction implements DialogAction {
    protected final boolean start;
    protected final boolean end;
    protected final boolean speak;

    public CloseInventoryDialogAction(boolean start, boolean end, boolean speak) {
        this.start = start;
        this.end = end;
        this.speak = speak;
    }

    @Override
    public void onStart(DialogPlayer player) {
        if(start){
            player.audience().forEach(e ->{
                if(e instanceof HumanEntity h) h.closeInventory();
            });
        }
    }

    @Override
    public void onEnd(DialogPlayer player) {
        if(end){
            player.audience().forEach(e ->{
                if(e instanceof HumanEntity h) h.closeInventory();
            });
        }
    }

    @Override
    public void onSpeak(DialogPlayer player) {
        if(speak){
            player.audience().forEach(e ->{
                if(e instanceof HumanEntity h) h.closeInventory();
            });
        }
    }
}
