package killercreepr.usurvive.values;

import killercreepr.crux.valueproviders.number.ConstantNumber;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.crux.valueproviders.number.UniformNumber;
import org.jetbrains.annotations.NotNull;

public class DefaultValues implements ValuesProvider {
    public static final NumberProvider DEATH_GUIDE_TICK_EVERY = new UniformNumber(60, 100);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_AMOUNT = new UniformNumber(3, 5);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_SHIFT_START = new UniformNumber(1D, 3D);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_SHIFT = new UniformNumber(1D, 3D);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER = new ConstantNumber(.3D);

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_TICK_EVERY() {
        return DEATH_GUIDE_TICK_EVERY;
    }

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_PARTICLE_AMOUNT() {
        return DEATH_GUIDE_PARTICLE_AMOUNT;
    }
}
