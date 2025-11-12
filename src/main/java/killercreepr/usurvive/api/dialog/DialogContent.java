package killercreepr.usurvive.api.dialog;

import killercreepr.crux.api.data.holder.DataInfoHolder;

import java.util.List;

public interface DialogContent extends DataInfoHolder {
    List<DialogLine> dialog();
    List<DialogAction> actions();
    int priority();
}
