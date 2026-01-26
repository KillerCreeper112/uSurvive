package killercreepr.usurvive.api.event;

import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DialogEvent extends Event {
  public static final HandlerList HANDLER_LIST = new HandlerList();

  protected final DialogPlayer dialogPlayer;
  protected final DialogContent dialogContent;

  public DialogEvent(DialogPlayer dialogPlayer, DialogContent dialogContent) {
    this.dialogPlayer = dialogPlayer;
    this.dialogContent = dialogContent;
  }

  public DialogPlayer getDialogPlayer() {
    return dialogPlayer;
  }

  public DialogContent getDialogContent() {
    return dialogContent;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }
  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
