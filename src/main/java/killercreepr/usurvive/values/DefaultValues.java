package killercreepr.usurvive.values;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.valueproviders.number.UniformNumber;
import killercreepr.usurvive.data.ResourcePackRequestData;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DefaultValues implements ValuesProvider {
    public static final Holder<String> SPAWN_WORLD = Holder.direct("world_spawn");
    public static final Holder<List<String>> FIRST_JOIN_COMMANDS = Holder.direct(List.of(
        "lp user <player_name> group add default_manage"
    ));
    public static final Holder<String> SPAWN_TELEPORT_MODULES = Holder.direct("dontmove,nocmds");
    public static final NumberProvider SPAWN_TELEPORT_TIME = NumberProvider.constant(100);
    public static final NumberProvider SPAWN_TELEPORT_MSG_INTERVAL = NumberProvider.constant(20);
    public static final NumberProvider DEATH_GUIDE_TICK_EVERY = new UniformNumber(60, 100);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_AMOUNT = new UniformNumber(3, 5);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_SHIFT_START = new UniformNumber(1D, 3D);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_SHIFT = new UniformNumber(1D, 3D);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER = new UniformNumber(.01, .03);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_OFFSET_SIDE = new UniformNumber(-.5, .5);
    public static final NumberProvider DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL = new UniformNumber(-.5, .5);
    public static final Holder<Map<String, List<String>>> DIMENSION_DISABLED_COMMANDS = Holder.direct(Map.of(
        "abyss", List.of("sethome")
    ));
    public static final NumberProvider ECHO_MAX_HEALTH = NumberProvider.equation(
        "10 + (<exp> / 50)"
    );
    public static final NumberProvider DEATH_EXPIRY_TIME = NumberProvider.constant(72000);

    @NotNull
    @Override
    public Holder<String> SPAWN_WORLD() {
        return SPAWN_WORLD;
    }

    @NotNull
    @Override
    public NumberProvider SPAWN_TELEPORT_TIME() {
        return SPAWN_TELEPORT_TIME;
    }

    @NotNull
    @Override
    public NumberProvider SPAWN_TELEPORT_MSG_INTERVAL() {
        return SPAWN_TELEPORT_MSG_INTERVAL;
    }

    @NotNull
    @Override
    public Holder<String> SPAWN_TELEPORT_MODULES() {
        return SPAWN_TELEPORT_MODULES;
    }

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

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_PARTICLE_SHIFT_START() {
        return DEATH_GUIDE_PARTICLE_SHIFT_START;
    }

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_PARTICLE_SHIFT() {
        return DEATH_GUIDE_PARTICLE_SHIFT;
    }

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER() {
        return DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER;
    }

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_PARTICLE_OFFSET_SIDE() {
        return DEATH_GUIDE_PARTICLE_OFFSET_SIDE;
    }

    @NotNull
    @Override
    public NumberProvider DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL() {
        return DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL;
    }

    @NotNull
    @Override
    public Holder<Map<String, List<String>>> DIMENSION_DISABLED_COMMANDS() {
        return DIMENSION_DISABLED_COMMANDS;
    }

    @NotNull
    @Override
    public NumberProvider ECHO_MAX_HEALTH() {
        return ECHO_MAX_HEALTH;
    }

    @NotNull
    @Override
    public Holder<List<String>> FIRST_JOIN_COMMANDS() {
        return FIRST_JOIN_COMMANDS;
    }

    @NotNull
    @Override
    public Holder<Location> RESPAWN_LOCATION() {
        return Holder.empty();
    }

    @NotNull
    @Override
    public NumberProvider DEATH_EXPIRY_TIME() {
        return DEATH_EXPIRY_TIME;
    }

    @NotNull
    @Override
    public Holder<Component> SERVER_MOTD() {
        return Holder.empty();
    }

    @NotNull
    @Override
    public Holder<ResourcePackRequestData> RESOURCE_PACKS() {
        return Holder.empty();
    }
}
