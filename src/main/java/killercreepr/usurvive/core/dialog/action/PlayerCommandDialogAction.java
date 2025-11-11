package killercreepr.usurvive.core.dialog.action;

import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerCommandDialogAction implements DialogAction {
    protected final List<String> start;
    protected final List<String> end;
    protected final List<String> speak;

    public PlayerCommandDialogAction(List<String> start, List<String> end, List<String> speak) {
        this.start = start;
        this.end = end;
        this.speak = speak;
    }

    @Override
    public void onStart(DialogPlayer player) {
        performCommand(start, player);
    }

    public void performCommand(List<String> cmds, DialogPlayer player){
        if(cmds == null) return;
        player.audience().forEach(audience -> {
            if(audience instanceof Player p){
                performCommand(cmds, p);
            }
        });
    }

    public void performCommand(List<String> cmds, Player p){
        cmds.forEach(s ->{
            p.performCommand(Crux.format().deserializeString(s, TagContainer.string().hook(p)));
        });
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
