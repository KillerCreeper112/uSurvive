package killercreepr.usurvive.api.guide;

import killercreepr.crux.api.data.WorldPosition;
import killercreepr.crux.core.data.SimpleWorldPosition;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.core.guide.SimpleGuidePoint;
import net.kyori.adventure.key.Key;

public interface GuidePoint {
    static GuidePoint guidePoint(WorldPosition pos, DialogContent dialog){
        return new SimpleGuidePoint(pos, dialog);
    }
    static GuidePoint guidePoint(WorldPosition pos){
        return guidePoint(pos, null);
    }

    WorldPosition pos();
    DialogContent dialog();

    default WorldPosition posWithDefaultWorld(Key key){
        var pos = pos();
        if(pos.worldKey() == null){
            return new SimpleWorldPosition(pos.x(), pos.y(), pos.z(), key);
        }
        return pos;
    }
}
