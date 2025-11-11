package killercreepr.usurvive.core.guide;

import killercreepr.crux.api.data.WorldPosition;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.guide.GuidePoint;

public class SimpleGuidePoint implements GuidePoint {
    protected final WorldPosition pos;
    protected final DialogContent dialog;
    protected final boolean keepMoving;

    public SimpleGuidePoint(WorldPosition pos, DialogContent dialog, boolean keepMoving) {
        this.pos = pos;
        this.dialog = dialog;
        this.keepMoving = keepMoving;
    }

    @Override
    public WorldPosition pos() {
        return pos;
    }

    @Override
    public DialogContent dialog() {
        return dialog;
    }

    @Override
    public boolean keepMoving() {
        return keepMoving;
    }
}
