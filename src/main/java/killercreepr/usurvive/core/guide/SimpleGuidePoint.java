package killercreepr.usurvive.core.guide;

import killercreepr.crux.api.data.WorldPosition;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.api.guide.GuidePoint;

import java.util.List;

public class SimpleGuidePoint implements GuidePoint {
    protected final WorldPosition pos;
    protected final List<DialogLine> dialog;
    protected final boolean keepMoving;

    public SimpleGuidePoint(WorldPosition pos, List<DialogLine> dialog, boolean keepMoving) {
        this.pos = pos;
        this.dialog = dialog;
        this.keepMoving = keepMoving;
    }

    @Override
    public WorldPosition pos() {
        return pos;
    }

    @Override
    public List<DialogLine> dialog() {
        return dialog;
    }

    @Override
    public boolean keepMoving() {
        return keepMoving;
    }
}
