package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.usurvive.api.dialog.DialogAction;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class WrappedDialogInfoContent extends WrappedDialogContent {
    protected final DataExchange info;
    protected final DataExchange mergedInfo;

    public WrappedDialogInfoContent(DialogContent child, DataExchange info) {
        super(child);
      this.info = info;
      this.mergedInfo = child.info().append(info);
    }

    @Override
    public @NonNull DataExchange info() {
        return mergedInfo;
    }
}
