package killercreepr.usurvive.core.dialog;

import killercreepr.crux.core.data.util.Pair;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogLine;

public class DialogContentLinePair extends Pair<DialogContent, DialogLine> {
    public DialogContentLinePair(DialogContent first, DialogLine second) {
        super(first, second);
    }
}
