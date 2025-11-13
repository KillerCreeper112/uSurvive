package killercreepr.usurvive.api.dialog;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.data.holder.DataInfoHolder;

import java.util.List;

public interface DialogContent extends DataInfoHolder {
    List<DialogLine> dialog();
    List<DialogAction> actions();
    int priority();

    default void sendInfo(DataExchange info){

    }

    default void onStart(DialogPlayer player){

    }
    default void onPause(DialogPlayer player){

    }

    default void onEnd(DialogPlayer player){

    }
}
