package killercreepr.usurvive.api.dialog;

import killercreepr.crux.api.data.holder.DataInfoHolder;

import java.util.List;

public interface DialogLine extends DataInfoHolder {
    String line();
    List<DialogAction> actions();
}
