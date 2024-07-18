package killercreepr.usurvive.values;

import killercreepr.crux.data.Reloadable;
import killercreepr.crux.valueproviders.number.NumberHolder;
import killercreepr.crux.valueproviders.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

public interface ValuesProvider extends Reloadable {
    @NotNull
    NumberHolder DEATH_GUIDE_TICK_EVERY();
    @NotNull
    NumberHolder DEATH_GUIDE_PARTICLE_AMOUNT();
    @NotNull
    NumberHolder DEATH_GUIDE_PARTICLE_SHIFT_START();
    @NotNull
    NumberHolder DEATH_GUIDE_PARTICLE_SHIFT();
    @NotNull
    NumberHolder DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER();
}
