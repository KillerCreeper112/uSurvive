package killercreepr.usurvive.config;

import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.cruxconfig.config.bukkit.file.Cfg;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.NumCfgValue;
import killercreepr.usurvive.values.DefaultValues;
import killercreepr.usurvive.values.ValuesProvider;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Config extends Cfg implements ValuesProvider {
    public final NumCfgValue DEATH_GUIDE_TICK_EVERY = new NumCfgValue(DefaultValues.DEATH_GUIDE_TICK_EVERY.value()){};
    public final NumCfgValue DEATH_GUIDE_PARTICLE_AMOUNT = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_AMOUNT.value()){};
    public final NumCfgValue DEATH_GUIDE_PARTICLE_SHIFT_START = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_SHIFT_START.value()){};
    public final NumCfgValue DEATH_GUIDE_PARTICLE_SHIFT = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_SHIFT.value()){};
    public final NumCfgValue DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER.value()){};
    public Config(@NotNull Plugin plugin, @NotNull String path) {
        super(plugin, path);
    }

    public Config(@NotNull File file) {
        super(file);
    }

    public Config(@NotNull CruxConfig cfg) {
        super(cfg);
    }


    @Override
    public void reload(@NotNull CruxPlugin plugin) {
        setup();
    }

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_TICK_EVERY() {
        return DEATH_GUIDE_TICK_EVERY.value();
    }

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_PARTICLE_AMOUNT() {
        return DEATH_GUIDE_PARTICLE_AMOUNT.value();
    }

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_PARTICLE_SHIFT_START() {
        return DEATH_GUIDE_PARTICLE_SHIFT_START.value();
    }

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_PARTICLE_SHIFT() {
        return DEATH_GUIDE_PARTICLE_SHIFT.value();
    }

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER() {
        return DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER.value();
    }
}
