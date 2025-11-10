package killercreepr.usurvive.api.values;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.data.WorldLocation;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.usurvive.api.guide.GuidePoint;

import java.util.List;

public interface GuideCfg {
    Holder<WorldLocation> GUIDE_1_SPAWN();
    Holder<List<GuidePoint>> GUIDE_1_POINTS();
    Holder<NumberProvider> GUIDE_1_MAX_PARTICLE_POINTS();
}
