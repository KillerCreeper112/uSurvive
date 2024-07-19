package killercreepr.usurvive.values;

import killercreepr.crux.valueproviders.number.NumberHolder;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.crux.valueproviders.number.UniformNumber;
import org.jetbrains.annotations.NotNull;

public class DefaultValues implements ValuesProvider {
    public static final NumberProvider DEATH_GUIDE_TICK_EVERY = new UniformNumber(60, 100);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_AMOUNT = new UniformNumber(3, 5);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_SHIFT_START = new UniformNumber(1D, 3D);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_SHIFT = new UniformNumber(1D, 3D);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER = new UniformNumber(.01, .03);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_OFFSET_SIDE = new UniformNumber(-.5, .5);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL = new UniformNumber(-.5, .5);

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_TICK_EVERY() {
        return DEATH_GUIDE_TICK_EVERY;
    }

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_PARTICLE_AMOUNT() {
        return DEATH_GUIDE_PARTICLE_AMOUNT;
    }

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_PARTICLE_SHIFT_START() {
        return DEATH_GUIDE_PARTICLE_SHIFT_START;
    }

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_PARTICLE_SHIFT() {
        return DEATH_GUIDE_PARTICLE_SHIFT;
    }

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER() {
        return DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER;
    }

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_PARTICLE_OFFSET_SIDE() {
        return DEATH_GUIDE_PARTICLE_OFFSET_SIDE;
    }

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL() {
        return DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL;
    }
}
