package killercreepr.usurvive.config;

import killercreepr.crux.data.Holder;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.util.CruxWorldUtil;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.cruxconfig.config.bukkit.file.Cfg;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.cruxconfig.config.bukkit.value.NumCfgValue;
import killercreepr.usurvive.values.DefaultValues;
import killercreepr.usurvive.values.ValuesProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Config extends Cfg implements ValuesProvider {
    public final CfgValue<String> SPAWN_WORLD = new CommonValue<>(DefaultValues.SPAWN_WORLD.value()){};
    public final CfgValue<String> RESPAWN_WORLD = new CommonValue<>(DefaultValues.SPAWN_WORLD.value()){};
    public final NumCfgValue RESPAWN_X = new NumCfgValue(NumberProvider.constant(0));
    public final NumCfgValue RESPAWN_Y = new NumCfgValue(NumberProvider.constant(0));
    public final NumCfgValue RESPAWN_Z = new NumCfgValue(NumberProvider.constant(0));
    public final NumCfgValue SPAWN_TELEPORT_TIME = new NumCfgValue(DefaultValues.SPAWN_TELEPORT_TIME);
    public final NumCfgValue SPAWN_TELEPORT_MSG_INTERVAL = new NumCfgValue(DefaultValues.SPAWN_TELEPORT_MSG_INTERVAL);
    public final CfgValue<String> SPAWN_TELEPORT_MODULES = new CommonValue<>(DefaultValues.SPAWN_TELEPORT_MODULES.value()){};
    public final NumCfgValue DEATH_GUIDE_TICK_EVERY = new NumCfgValue(DefaultValues.DEATH_GUIDE_TICK_EVERY);
    public final NumCfgValue DEATH_GUIDE_PARTICLE_AMOUNT = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_AMOUNT);
    public final NumCfgValue DEATH_GUIDE_PARTICLE_SHIFT_START = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_SHIFT_START);
    public final NumCfgValue DEATH_GUIDE_PARTICLE_SHIFT = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_SHIFT);
    public final NumCfgValue DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER);
    public final NumCfgValue DEATH_GUIDE_PARTICLE_OFFSET_SIDE = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_OFFSET_SIDE);
    public final NumCfgValue DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL = new NumCfgValue(DefaultValues.DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL);
    public final CommonValue<Map<String, List<String>>> DIMENSION_DISABLED_COMMANDS = new CommonValue<>(DefaultValues.DIMENSION_DISABLED_COMMANDS.value()){};
    public final NumCfgValue ECHO_MAX_HEALTH = new NumCfgValue(DefaultValues.ECHO_MAX_HEALTH);
    public final CfgValue<List<String>> FIRST_JOIN_COMMANDS = new CommonValue<>(DefaultValues.FIRST_JOIN_COMMANDS.value()){};
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
    public Holder<String> SPAWN_WORLD() {
        return SPAWN_WORLD;
    }

    @NotNull
    @Override
    public NumberProvider SPAWN_TELEPORT_TIME() {
        return SPAWN_TELEPORT_TIME.value();
    }

    @NotNull
    @Override
    public NumberProvider SPAWN_TELEPORT_MSG_INTERVAL() {
        return SPAWN_TELEPORT_MSG_INTERVAL.value();
    }

    @NotNull
    @Override
    public Holder<String> SPAWN_TELEPORT_MODULES() {
        return SPAWN_TELEPORT_MODULES;
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

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_PARTICLE_OFFSET_SIDE() {
        return DEATH_GUIDE_PARTICLE_OFFSET_SIDE.value();
    }

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL() {
        return DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL.value();
    }

    @NotNull
    @Override
    public Holder<Map<String, List<String>>> DIMENSION_DISABLED_COMMANDS() {
        return DIMENSION_DISABLED_COMMANDS;
    }

    @NotNull
    @Override
    public NumberProvider ECHO_MAX_HEALTH() {
        return ECHO_MAX_HEALTH.value();
    }

    @NotNull
    @Override
    public Holder<List<String>> FIRST_JOIN_COMMANDS() {
        return FIRST_JOIN_COMMANDS;
    }

    @NotNull
    @Override
    public Holder<Location> RESPAWN_LOCATION() {
        return () ->{
            String name = RESPAWN_WORLD.value();
            World world = name == null ? null : CruxWorldUtil.getOrLoadWorld(name);
            if(world== null) return null;
            return new Location(world, RESPAWN_X.getDouble(), RESPAWN_Y.getDouble(), RESPAWN_Z.getDouble());
        };
    }
}
