package killercreepr.usurvive.api.guide;

import killercreepr.crux.api.data.WorldPosition;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.core.guide.SimpleGuidePoint;

public interface GuidePoint {
    static GuidePoint guidePoint(WorldPosition pos, DialogContent dialog, boolean keepMoving){
        return new SimpleGuidePoint(pos, dialog, keepMoving);
    }
    static GuidePoint guidePoint(WorldPosition pos, DialogContent dialog){
        return guidePoint(pos, dialog, false);
    }
    static GuidePoint guidePoint(WorldPosition pos){
        return guidePoint(pos, null);
    }

    WorldPosition pos();
    DialogContent dialog();
    boolean keepMoving();
}
