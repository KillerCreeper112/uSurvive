package killercreepr.usurvive.config;

import killercreepr.crux.data.Holder;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.valueproviders.number.NumberHolder;
import killercreepr.cruxconfig.config.bukkit.file.Cfg;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.cruxconfig.config.bukkit.value.NumCfgValue;
import killercreepr.usurvive.values.DefaultValues;
import killercreepr.usurvive.values.ValuesProvider;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Config extends Cfg implements ValuesProvider {
    public final NumCfgValue DEATH_GUIDE_TICK_EVERY = new NumCfgValue(DefaultValues.DEATH_GUIDE_TICK_EVERY){};
    public final NumCfgValue DEATH_GUIDE_PARTICLE_AMOUNT = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_AMOUNT){};
    public final NumCfgValue DEATH_GUIDE_PARTICLE_SHIFT_START = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_SHIFT_START){};
    public final NumCfgValue DEATH_GUIDE_PARTICLE_SHIFT = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_SHIFT){};
    public final NumCfgValue DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER){};
    public final NumCfgValue DEATH_GUIDE_PARTICLE_OFFSET_SIDE = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_OFFSET_SIDE){};
    public final NumCfgValue DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL){};
    public final CommonValue<Map<String, List<String>>> DIMENSION_DISABLED_COMMANDS = new CommonValue<>(DefaultValues.DIMENSION_DISABLED_COMMANDS.value()){};
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
    public NumberHolder DEATH_GUIDE_TICK_EVERY() {
        return DEATH_GUIDE_TICK_EVERY.value();
    }

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_PARTICLE_AMOUNT() {
        return DEATH_GUIDE_PARTICLE_AMOUNT.value();
    }

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_PARTICLE_SHIFT_START() {
        return DEATH_GUIDE_PARTICLE_SHIFT_START.value();
    }

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_PARTICLE_SHIFT() {
        return DEATH_GUIDE_PARTICLE_SHIFT.value();
    }

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER() {
        return DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER.value();
    }

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_PARTICLE_OFFSET_SIDE() {
        return DEATH_GUIDE_PARTICLE_OFFSET_SIDE.value();
    }

    @NotNull
    @Override
    public NumberHolder DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL() {
        return DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL.value();
    }

    @NotNull
    @Override
    public Holder<Map<String, List<String>>> DIMENSION_DISABLED_COMMANDS() {
        return DIMENSION_DISABLED_COMMANDS;
    }
}
