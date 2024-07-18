package killercreepr.usurvive.values;

import killercreepr.crux.data.Reloadable;
import killercreepr.crux.valueproviders.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

public interface ValuesProvider extends Reloadable {
    @NotNull
    NumberProvider DEATH_GUIDE_TICK_EVERY();
    @NotNull
    NumberProvider DEATH_GUIDE_PARTICLE_AMOUNT();
    @NotNull
    NumberProvider DEATH_GUIDE_PARTICLE_SHIFT_START();
    @NotNull
    NumberProvider DEATH_GUIDE_PARTICLE_SHIFT();
    @NotNull
    NumberProvider DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER();
}
