package killercreepr.usurvive.core.values;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.item.predicate.ItemPredicate;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.valueproviders.number.UniformNumber;
import killercreepr.cruxstructures.api.structure.generation.StructureGenerator;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.data.CfgLootData;
import killercreepr.usurvive.core.data.PremiumRank;
import killercreepr.usurvive.core.data.ResourcePackRequestData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DefaultValues implements ValuesProvider {
    public static final Holder<String> SPAWN_WORLD = Holder.direct("world_spawn");
    public static final Holder<List<String>> FIRST_JOIN_COMMANDS = Holder.direct(List.of(
        "lp user <player_name> group add default_manage"
    ));
    public static final Holder<String> SPAWN_TELEPORT_COMPONENTS = Holder.direct("[teleport/waiting_time=100,teleport/dont_move={},teleport/disable_commands={},teleport/msg={interval:20}]");
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
    public Holder<List<PremiumRank>> PREMIUM_RANKS() {
        return null;
    }

    @NotNull
    @Override
    public Holder<String> SPAWN_WORLD() {
        return SPAWN_WORLD;
    }
    @NotNull
    @Override
    public Holder<String> SPAWN_TELEPORT_COMPONENTS() {
        return SPAWN_TELEPORT_COMPONENTS;
    }

    @NotNull
    @Override
    public Holder<String> PLAYER_TAB_NAME() {
        return null;
    }

    @NotNull
    @Override
    public Holder<String> PLAYER_NAME_TAG() {
        return null;
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
    public NumberProvider DEATH_GRAVE_SPAWN_SEARCH_RANGE() {
        return null;
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

    @NotNull
    @Override
    public NumberProvider RESOURCE_OVERWORLD_BORDER() {
        return null;
    }

    @NotNull
    @Override
    public NumberProvider RESOURCE_NETHER_BORDER() {
        return null;
    }

    @NotNull
    @Override
    public NumberProvider RESOURCE_OVERWORLD_RESET_TIME() {
        return null;
    }

    @NotNull
    @Override
    public NumberProvider RESET_SET_RANDOM_TP_TIME() {
        return null;
    }

    @NotNull
    @Override
    public NumberProvider TPA_EXPIRE_TICKS() {
        return null;
    }

    @NotNull
    @Override
    public NumberProvider TPA_CONFIRM_EXPIRE_TICKS() {
        return null;
    }

    @NotNull
    @Override
    public Holder<String> TPA_TELEPORT_COMPONENTS() {
        return null;
    }

    @NotNull
    @Override
    public NumberProvider UPDATE_TAB_TIME() {
        return null;
    }

    @NotNull
    @Override
    public Holder<String> TAB_HEADER() {
        return null;
    }

    @NotNull
    @Override
    public Holder<String> TAB_FOOTER() {
        return null;
    }

    @NotNull
    @Override
    public Holder<List<String>> INFUSED_POTIONS_TAG_FORMAT() {
        return null;
    }

    @NotNull
    @Override
    public Holder<List<String>> INFUSED_POTIONS_EFFECTS_LIST_TAG_FORMAT() {
        return null;
    }

    @NotNull
    @Override
    public Holder<ItemPredicate> APPLICABLE_INFUSION_BENEFITS() {
        return null;
    }

    @NotNull
    @Override
    public NumberProvider INFUSION_DURABILITY_PERCENT() {
        return null;
    }

    @NotNull
    @Override
    public Holder<Map<Key, Double>> INFUSION_ATTRIBUTE_BENEFITS() {
        return null;
    }

    @NotNull
    @Override
    public NumberProvider SLEEPER_WORLD_TIME_SKIP() {
        return null;
    }

    @Override
    public NumberProvider SLEEP_PREVENT_IF_ATTACKED_WITHIN() {
        return null;
    }

    @Override
    public Holder<Collection<CfgLootData>> CHESTS_LOOT_DATA() {
        return null;
    }

    @Override
    public Holder<Collection<CfgLootData>> BLOCK_DISPENSE_LOOT_DATA() {
        return null;
    }

    @Override
    public Holder<StructureGenerator> GUIDE_RESOURCE_WORLD_STRUCTURE() {
        return null;
    }

    @Override
    public Holder<NumberProvider> GUIDE_RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE() {
        return null;
    }

    @NotNull
    @Override
    public NumberProvider ANIMAL_AGGRESSION_CHANCE() {
        return null;
    }
}
