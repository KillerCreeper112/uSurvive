package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.core.lang.Lang;

public class UserDialogPlayer extends SimpleDialogPlayer{
    @Override
    public void onDialogCompleteLine(DialogLine line) {
        super.onDialogCompleteLine(line);
        audience().forEach(audience ->{
            Lang.DIALOG_LINE_COMPLETE.use(audience, TagContainer.merged().hook(new DialogContentLinePair(getDialog(), line)));
        });
    }
}
