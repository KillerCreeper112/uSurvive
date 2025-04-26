package killercreepr.usurvive.core.statistic;

import killercreepr.crux.core.Crux;
import killercreepr.cruxstatistics.api.statistic.CruxStatisticType;
import killercreepr.cruxstatistics.core.registries.CruxStatisticRegistries;

public class USurviveStatistics {
    public static void register(){}

    public static final CruxStatisticType<?> IDLE_TIME = reg(CruxStatisticType.statisticType(Crux.key("idle_time")));

    private static CruxStatisticType<?> reg(CruxStatisticType<?> stat){
        return CruxStatisticRegistries.STATISTIC_TYPE.register(stat);
    }
}
