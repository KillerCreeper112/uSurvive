package killercreepr.usurvive.api.dialog;

import java.util.List;

public interface DialogLine {
    String line();
    List<DialogAction> actions();
}
