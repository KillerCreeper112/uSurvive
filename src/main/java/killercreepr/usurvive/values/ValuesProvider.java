package killercreepr.usurvive.values;

import killercreepr.crux.data.Holder;
import killercreepr.crux.data.Reloadable;
import killercreepr.crux.valueproviders.number.NumberHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

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
    @NotNull
    NumberHolder DEATH_GUIDE_PARTICLE_OFFSET_SIDE();
    @NotNull
    NumberHolder DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL();
    @NotNull
    Holder<Map<String, List<String>>> DIMENSION_DISABLED_COMMANDS();
}
