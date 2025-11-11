package killercreepr.usurvive.api.dialog;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface DialogPlayer {
    DialogContent getDialog();
    void setDialog(DialogContent dialog);
    void onDialogStartLine(DialogLine line);

    DialogLine getCurrentLine();

    void queueDialog(DialogContent dialog);

    void onDialogDisplay(Player user);
    void dialogTick(Player... user);
    String getCurrentDialogLine();
    int getDialogWaitTime();

    int getDialogWaiting();
    void setDialogWaiting(int time);

    int getDialogSplitSpeed();
    void setDialogSplitSpeed(int speed);

    void setDialogWaitTime(int time);

    void addAudience(Audience... audiences);
    void removeAudience(Audience... audiences);
    boolean hasAudience(Audience audience);
    Collection<Audience> audience();
}
