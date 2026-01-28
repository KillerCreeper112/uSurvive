package killercreepr.usurvive.api.event;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogPlayer;

public class DialogSendContentInfoEvent extends DialogEvent{
  protected final DataExchange info;
  public DialogSendContentInfoEvent(DialogPlayer dialogPlayer, DialogContent dialogContent, DataExchange info) {
    super(dialogPlayer, dialogContent);
    this.info = info;
  }

  public DataExchange getInfo() {
    return info;
  }
}
