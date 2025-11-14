package killercreepr.usurvive.api.story;

import killercreepr.usurvive.api.dialog.DialogContent;
import org.bukkit.entity.Player;

public interface StoryStep {
    boolean canStart(StoryState state);
    boolean isComplete(StoryState state);

    DialogContent getDialog();

    void onStart(Player p);
    void onComplete(Player p);
}
