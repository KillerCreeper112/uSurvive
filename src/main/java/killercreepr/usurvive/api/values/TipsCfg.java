package killercreepr.usurvive.api.values;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import net.kyori.adventure.key.Key;

import java.util.Map;

public interface TipsCfg {
    NumberProvider RANDOM_TIP_TIME();
    NumberProvider RANDOM_TIP_TIME_OCCASIONAL();
}
