package killercreepr.usurvive.api.dialog;

import killercreepr.crux.api.data.holder.DataInfoHolder;

import java.util.List;

public interface Dialoger extends DataInfoHolder {
    List<DialogContent> dialogContent();
    List<DialogAction> actions();
}
