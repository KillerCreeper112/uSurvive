package killercreepr.usurvive.api.story;

import killercreepr.usurvive.api.dialog.DialogContent;
import org.bukkit.entity.Player;

public interface StoryStep {
    boolean canStart(StoryState state);
    boolean isComplete(StoryState state);

    default DialogContent getDialog(){ return null; }

    default void onStart(Player p){}
    default void onComplete(Player p){}
}
