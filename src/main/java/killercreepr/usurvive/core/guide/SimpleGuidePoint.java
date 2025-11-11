package killercreepr.usurvive.core.guide;

import killercreepr.crux.api.data.WorldPosition;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.guide.GuidePoint;

public class SimpleGuidePoint implements GuidePoint {
    protected final WorldPosition pos;
    protected final DialogContent dialog;

    public SimpleGuidePoint(WorldPosition pos, DialogContent dialog) {
        this.pos = pos;
        this.dialog = dialog;
    }

    @Override
    public WorldPosition pos() {
        return pos;
    }

    @Override
    public DialogContent dialog() {
        return dialog;
    }
}
