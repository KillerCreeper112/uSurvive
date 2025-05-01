package killercreepr.usurvive.api.values;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import net.kyori.adventure.key.Key;

import java.util.Map;

public interface GravestoneCfg {
    CfgValue<Map<Key, String>> GRAVESTONE_COSTS();
    NumberProvider REDUCE_RECENT_DEATHS_TIME();
    NumberProvider MAX_RECENT_DEATHS();
}
