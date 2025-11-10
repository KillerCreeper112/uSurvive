package killercreepr.usurvive.api.dialog;

import org.bukkit.entity.Player;

import java.util.List;

public interface DialogPlayer {
    List<DialogLine> getDialog();
    void setDialog(List<DialogLine> dialog);
    void onNextLine(DialogLine line);

    void queueDialog(List<DialogLine> dialog);

    void onDialogDisplay(Player user);
    void dialogTick(Player... user);
    String getCurrentDialogLine();
    int getDialogWaitTime();

    int getDialogWaiting();
    void setDialogWaiting(int time);

    int getDialogSplitSpeed();
    void setDialogSplitSpeed(int speed);

    void setDialogWaitTime(int time);

}
