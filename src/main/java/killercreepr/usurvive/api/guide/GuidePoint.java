package killercreepr.usurvive.api.guide;

import killercreepr.crux.api.data.WorldPosition;
import killercreepr.usurvive.core.guide.SimpleGuidePoint;

import java.util.List;

public interface GuidePoint {
    static GuidePoint guidePoint(WorldPosition pos, List<String> dialog, boolean keepMoving){
        return new SimpleGuidePoint(pos, dialog, keepMoving);
    }
    static GuidePoint guidePoint(WorldPosition pos, List<String> dialog){
        return guidePoint(pos, dialog, false);
    }
    static GuidePoint guidePoint(WorldPosition pos){
        return guidePoint(pos, null);
    }

    WorldPosition pos();
    List<String> dialog();
    boolean keepMoving();
}
