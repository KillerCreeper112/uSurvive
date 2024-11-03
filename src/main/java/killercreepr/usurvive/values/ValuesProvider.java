package killercreepr.usurvive.values;

import killercreepr.crux.data.Holder;
import killercreepr.crux.data.Reloadable;
import killercreepr.crux.valueproviders.number.NumberProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface ValuesProvider extends Reloadable {
    @NotNull
    Holder<String> SPAWN_WORLD();
    @NotNull
    NumberProvider SPAWN_TELEPORT_TIME();
    @NotNull
    NumberProvider SPAWN_TELEPORT_MSG_INTERVAL();
    @NotNull
    Holder<String> SPAWN_TELEPORT_MODULES();
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
    @NotNull
    NumberProvider DEATH_GUIDE_PARTICLE_OFFSET_SIDE();
    @NotNull
    NumberProvider DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL();
    @NotNull
    Holder<Map<String, List<String>>> DIMENSION_DISABLED_COMMANDS();
    @NotNull
    NumberProvider ECHO_MAX_HEALTH();
    @NotNull Holder<List<String>> FIRST_JOIN_COMMANDS();
    @NotNull Holder<Location> RESPAWN_LOCATION();
    @NotNull
    NumberProvider DEATH_EXPIRY_TIME();
    @NotNull
    Holder<Component> SERVER_MOTD();
}
