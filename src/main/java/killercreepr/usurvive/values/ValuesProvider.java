package killercreepr.usurvive.values;

import killercreepr.crux.data.Reloadable;
import killercreepr.crux.valueproviders.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

public interface ValuesProvider extends Reloadable {
    @NotNull
    NumberProvider DEATH_GUIDE_TICK_EVERY();
}
