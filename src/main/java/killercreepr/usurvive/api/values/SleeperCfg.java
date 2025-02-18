package killercreepr.usurvive.api.values;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeakness;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessCalculator;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessWarning;

import java.util.Map;

//1 hour = 50 seconds
//50 seconds = 1000 ticks
//10000 hours
public interface SleeperCfg {
    //if slumber ticks reaches this value, then start sleep weakness
    //def=10,000
    NumberProvider SLEEP_WEAKNESS_SLUMBER_TICK_START();

    //start, for every tick the player sleeps, the player must not sleep for 1 second to remove a slumber tick.
    //def=20
    NumberProvider SLEEP_WEAKNESS_SLUMBER_DROP_OFF();
    //def=1
    NumberProvider SLEEP_WEAKNESS_SLUMBER_DROP_OFF_AMOUNT();
    //end
    Holder<Map<Integer, SleepWeakness>> SLEEP_WEAKNESS_LEVELS();
    Holder<SleepWeaknessCalculator> SLEEP_WEAKNESS_LEVELS_DEFINITION();
    Holder<SleepWeaknessWarning> SLEEP_WEAKNESS_WARNINGS();

    NumberProvider DEEP_SLEEP_START_TIME();

    NumberProvider SLEEP_REGENERATION_RATE();
    NumberProvider SLEEP_REGENERATION_AMOUNT();

    NumberProvider SLEEP_HUNGER_RATE();
    NumberProvider SLEEP_HUNGER_AMOUNT();
    NumberProvider SLEEP_HUNGER_SATURATION_RATE();
    NumberProvider SLEEP_HUNGER_SATURATION_AMOUNT();
}
