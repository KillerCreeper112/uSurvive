package killercreepr.usurvive.core.stat;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.cruxstats.api.stat.CruxStat;
import killercreepr.cruxstats.core.registries.CruxStatRegistries;
import killercreepr.usurvive.core.USurvivePlugin;

public class USurviveStats {
    public static void register(){}

    public static final CruxStat MAX_GRAVESTONES = reg(CruxStat.stat(
        Crux.key("max_gravestones"), NumberProvider.constant(2)
    ));
    public static final CruxStat GRAVESTONE_EXPIRY_TIME = reg(CruxStat.stat(
        Crux.key("gravestone_expiry_time"), NumberProvider.holder(() -> USurvivePlugin.inst().values().DEATH_EXPIRY_TIME().value())
    ));

    private static CruxStat reg(CruxStat stat){
        return CruxStatRegistries.STAT.register(stat);
    }
}
