package killercreepr.usurvive.api.values;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.data.WorldLocation;
import killercreepr.crux.api.data.WorldPosition;
import killercreepr.usurvive.api.guide.GuidePoint;

import java.util.List;

public interface GuideCfg {
    Holder<WorldLocation> GUIDE_1_SPAWN();
    Holder<WorldPosition> GUIDE_1_NPC_SPAWN();
    Holder<List<GuidePoint>> GUIDE_1_POINTS();
    Holder<List<String>> GUIDE_1_DIALOG();
}
