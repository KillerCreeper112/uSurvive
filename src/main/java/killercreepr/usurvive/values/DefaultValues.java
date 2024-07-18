package killercreepr.usurvive.values;

import killercreepr.crux.valueproviders.number.ConstantNumber;
import killercreepr.crux.valueproviders.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

public class DefaultValues implements ValuesProvider {
    public static final NumberProvider DEATH_GUIDE_TICK_EVERY = new ConstantNumber(20);
    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_TICK_EVERY() {
        return DEATH_GUIDE_TICK_EVERY;
    }
}
