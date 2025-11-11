package killercreepr.usurvive.api.guide;

import killercreepr.crux.api.data.WorldPosition;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.core.guide.SimpleGuidePoint;

public interface GuidePoint {
    static GuidePoint guidePoint(WorldPosition pos, DialogContent dialog){
        return new SimpleGuidePoint(pos, dialog);
    }
    static GuidePoint guidePoint(WorldPosition pos){
        return guidePoint(pos, null);
    }

    WorldPosition pos();
    DialogContent dialog();
}
