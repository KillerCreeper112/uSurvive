package killercreepr.usurvive.api.values;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.data.Reloadable;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.usurvive.core.data.ResourcePackRequestData;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface ValuesProvider extends Reloadable {
    @NotNull
    Holder<String> SPAWN_WORLD();
    @NotNull
    Holder<String> SPAWN_TELEPORT_COMPONENTS();
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
    NumberProvider DEATH_GRAVE_SPAWN_SEARCH_RANGE();
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
    @NotNull
    Holder<ResourcePackRequestData> RESOURCE_PACKS();

    @NotNull NumberProvider RESOURCE_OVERWORLD_BORDER();

    @NotNull NumberProvider TPA_EXPIRE_TICKS();
    @NotNull NumberProvider TPA_CONFIRM_EXPIRE_TICKS();
    @NotNull Holder<String> TPA_TELEPORT_COMPONENTS();
    @NotNull NumberProvider UPDATE_TAB_TIME();
    @NotNull Holder<String> TAB_HEADER();
    @NotNull Holder<String> TAB_FOOTER();
}
