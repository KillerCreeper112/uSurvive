package killercreepr.usurvive.core.guide.progression;

import killercreepr.crux.core.Crux;
import killercreepr.usurvive.core.dialog.UserDialogPlayer;
import org.bukkit.entity.Player;

public abstract class GenericGuideProgression implements GuideProgression {
    protected UserDialogPlayer dialogPlayer = new UserDialogPlayer();

    @Override
    public void onLoaded(Player p) {
        dialogPlayer.addAudience(p);
    }

    public UserDialogPlayer getDialogPlayer() {
        return dialogPlayer;
    }

    @Override
    public void onTick(Player p) {
        if(Crux.getCurrentTick() % 2 == 0) dialogPlayer.dialogTick(p);
    }
}
