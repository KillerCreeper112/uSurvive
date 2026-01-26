package killercreepr.usurvive.api.event;

import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogPlayer;

public class DialogNaturallyEndEvent extends DialogEvent{
  public DialogNaturallyEndEvent(DialogPlayer dialogPlayer, DialogContent dialogContent) {
    super(dialogPlayer, dialogContent);
  }
}
