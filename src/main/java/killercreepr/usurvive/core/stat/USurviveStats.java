package killercreepr.usurvive.core.stat;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.cruxstats.api.stat.CruxStat;
import killercreepr.cruxstats.core.registries.CruxStatRegistries;

public class USurviveStats {
    public static void register(){}

    public static final CruxStat MAX_GRAVESTONES = reg(CruxStat.stat(
        Crux.key("max_gravestones"), NumberProvider.constant(3)
    ));

    private static CruxStat reg(CruxStat stat){
        return CruxStatRegistries.STAT.register(stat);
    }
}
