package killercreepr.usurvive.core.config;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.item.predicate.ItemPredicate;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.util.CruxWorldUtil;
import killercreepr.cruxconfig.config.bukkit.file.Cfg;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.cruxconfig.config.bukkit.value.NumCfgValue;
import killercreepr.usurvive.api.values.SleeperCfg;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.data.CfgLootData;
import killercreepr.usurvive.core.data.PremiumRank;
import killercreepr.usurvive.core.data.ResourcePackRequestData;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeakness;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessCalculator;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessWarning;
import killercreepr.usurvive.core.values.DefaultValues;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@killercreepr.cruxconfig.config.common.annotations.Config(autoUpdate = true)
public class Config extends Cfg implements ValuesProvider, SleeperCfg {
    public final CfgValue<List<PremiumRank>> PREMIUM_RANKS = new CommonValue<>(){};
    public final CfgValue<String> SPAWN_WORLD = new CommonValue<>(DefaultValues.SPAWN_WORLD.value()){};
    public final CfgValue<String> RESPAWN_WORLD = new CommonValue<>(DefaultValues.SPAWN_WORLD.value()){};
    public final NumCfgValue RESPAWN_X = new NumCfgValue(NumberProvider.constant(0));
    public final NumCfgValue RESPAWN_Y = new NumCfgValue(NumberProvider.constant(0));
    public final NumCfgValue RESPAWN_Z = new NumCfgValue(NumberProvider.constant(0));
    public final CfgValue<String> SPAWN_TELEPORT_COMPONENTS = new CommonValue<>(DefaultValues.SPAWN_TELEPORT_COMPONENTS.value()){};
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
    public final NumCfgValue DEATH_EXPIRY_TIME = new NumCfgValue(DefaultValues.DEATH_EXPIRY_TIME);
    public final CfgValue<Component> SERVER_MOTD = new CommonValue<>(){};
    public final CfgValue<ResourcePackRequestData> RESOURCE_PACKS = new CommonValue<>(){};
    public final NumCfgValue RESOURCE_OVERWORLD_BORDER = new NumCfgValue(20000);
    public final NumCfgValue RESOURCE_NETHER_BORDER = new NumCfgValue(20000);
    public final NumCfgValue DEATH_GRAVE_SPAWN_SEARCH_RANGE = new NumCfgValue(80);
    public final NumCfgValue TPA_EXPIRE_TICKS = new NumCfgValue(1200);
    public final NumCfgValue TPA_CONFIRM_EXPIRE_TICKS = new NumCfgValue(300);
    public final NumCfgValue UPDATE_TAB_TIME = new NumCfgValue(100);
    public final CfgValue<String> TPA_TELEPORT_COMPONENTS = new CommonValue<>("[teleport/waiting_time=100,teleport/dont_move={},teleport/disable_commands={},teleport/msg={interval:20}]"){};
    public final CfgValue<String> TAB_HEADER = new CommonValue<>("\n<#FFCC09><bold>usurvive.net\n"){};
    public final CfgValue<String> TAB_FOOTER = new CommonValue<>(
        """
            <#F6D496>Orbits: <#F5E8D0>{{<player_crux_balance:orbit>}[0]} <gray>| <dark_aqua>Players: <aqua><server_players>
            <gold>Playtime: <yellow><duration:"{{<player_stat:play_time> * 50}}":sec:false:, :"%dd ":"%hh ":"%mm">
            """
    ){};
    public final CfgValue<List<String>> INFUSED_POTIONS_TAG_FORMAT = new CommonValue<>(List.of(
        "",
        "<gray><crux_slot_group_when_in_slot>:"
    )){};
    public final CfgValue<List<String>> INFUSED_POTIONS_EFFECTS_LIST_TAG_FORMAT = new CommonValue<>(List.of(
        "{e{IF(\"<potion_effect_harmful>\" == \"true\", \"<red>\", \"<blue>\")}}<potion_effect_name>{e{IF(<potion_effect_amplifier> > 0, \" <roman_numeral:{{<potion_effect_amplifier> + 1}}>\", \"\")}} (<duration:{{<potion_effect_duration>*50}}:days,hours:true:\":\":\"\":\"\":\"<math:less:%m:10:0>%m\":\"<math:less:%s:10:0>%s\">)"
    )){};
    public final NumCfgValue INFUSION_DURABILITY_PERCENT = new NumCfgValue(.5f);
    public final CfgValue<ItemPredicate> APPLICABLE_INFUSION_BENEFITS = new CommonValue<>(){};
    public final CfgValue<Map<Key, Double>> INFUSION_ATTRIBUTE_BENEFITS = new CommonValue<>(Map.of(Key.key("armor"), 1D)){};
    public final NumCfgValue RESOURCE_OVERWORLD_RESET_TIME = new NumCfgValue(72_000);

    public final NumCfgValue SLEEP_PREVENT_IF_ATTACKED_WITHIN = new NumCfgValue(100);

    public final NumCfgValue SLEEP_WEAKNESS_SLUMBER_DROP_OFF = new NumCfgValue(20);
    public final NumCfgValue SLEEP_WEAKNESS_SLUMBER_DROP_OFF_AMOUNT = new NumCfgValue(1);
    public final CfgValue<Map<Byte, SleepWeakness>> SLEEP_WEAKNESS_LEVELS = new CommonValue<>(){};
    public final CfgValue<SleepWeaknessCalculator> SLEEP_WEAKNESS_LEVELS_DEFINITION = new CommonValue<>(){};
    public final CfgValue<SleepWeaknessWarning> SLEEP_WEAKNESS_WARNINGS = new CommonValue<>(){};
    public final NumCfgValue DEEP_SLEEP_START_TIME = new NumCfgValue(80);
    public final NumCfgValue SLEEP_REGENERATION_RATE = new NumCfgValue(NumberProvider.uniform(10, 15));
    public final NumCfgValue SLEEP_REGENERATION_AMOUNT = new NumCfgValue(1);
    public final NumCfgValue SLEEP_HUNGER_RATE = new NumCfgValue(20);
    public final NumCfgValue SLEEP_HUNGER_AMOUNT = new NumCfgValue(-1);
    public final NumCfgValue SLEEP_HUNGER_SATURATION_RATE = new NumCfgValue(20);
    public final NumCfgValue SLEEP_HUNGER_SATURATION_AMOUNT = new NumCfgValue(-1);
    public final NumCfgValue SLEEPER_WORLD_TIME_SKIP = new NumCfgValue(1);
    public final NumCfgValue AUTO_KICK_SLEEPER_AFTER_TIME_PASSED = new NumCfgValue(24000);
    public final CfgValue<String> PLAYER_TAB_NAME = new CommonValue<>(){};
    public final CfgValue<String> PLAYER_NAME_TAG = new CommonValue<>(){};
    public final CfgValue<Collection<CfgLootData>> CHESTS_LOOT_DATA = new CommonValue<>(){};
    public final CfgValue<Collection<CfgLootData>> BLOCK_DISPENSE_LOOT_DATA = new CommonValue<>(){};
    public final CfgValue<Map<String, List<String>>> HELP_CMDS = new CommonValue<>(){};
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
        reload();
        setup();
    }

    @Override
    public NumberProvider DEEP_SLEEP_START_TIME() {
        return DEEP_SLEEP_START_TIME.value();
    }

    @Override
    public NumberProvider SLEEP_REGENERATION_RATE() {
        return SLEEP_REGENERATION_RATE.value();
    }

    @Override
    public NumberProvider SLEEP_REGENERATION_AMOUNT() {
        return SLEEP_REGENERATION_AMOUNT.value();
    }

    @Override
    public NumberProvider SLEEP_HUNGER_RATE() {
        return SLEEP_HUNGER_RATE.value();
    }

    @Override
    public NumberProvider SLEEP_HUNGER_AMOUNT() {
        return SLEEP_HUNGER_AMOUNT.value();
    }

    @Override
    public NumberProvider SLEEP_HUNGER_SATURATION_RATE() {
        return SLEEP_HUNGER_SATURATION_RATE.value();
    }

    @Override
    public NumberProvider SLEEP_HUNGER_SATURATION_AMOUNT() {
        return SLEEP_HUNGER_SATURATION_AMOUNT.value();
    }

    @Override
    public NumberProvider AUTO_KICK_SLEEPER_AFTER_TIME_PASSED() {
        return AUTO_KICK_SLEEPER_AFTER_TIME_PASSED.value();
    }

    @Override
    public NumberProvider SLEEP_WEAKNESS_SLUMBER_DROP_OFF() {
        return SLEEP_WEAKNESS_SLUMBER_DROP_OFF.value();
    }

    @Override
    public NumberProvider SLEEP_WEAKNESS_SLUMBER_DROP_OFF_AMOUNT() {
        return SLEEP_WEAKNESS_SLUMBER_DROP_OFF_AMOUNT.value();
    }

    @Override
    public Holder<Map<Byte, SleepWeakness>> SLEEP_WEAKNESS_LEVELS() {
        return SLEEP_WEAKNESS_LEVELS;
    }

    @Override
    public Holder<SleepWeaknessCalculator> SLEEP_WEAKNESS_LEVELS_DEFINITION() {
        return SLEEP_WEAKNESS_LEVELS_DEFINITION;
    }

    @Override
    public Holder<SleepWeaknessWarning> SLEEP_WEAKNESS_WARNINGS() {
        return SLEEP_WEAKNESS_WARNINGS;
    }

    @NotNull
    @Override
    public Holder<List<PremiumRank>> PREMIUM_RANKS() {
        return PREMIUM_RANKS;
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
        return PLAYER_TAB_NAME;
    }

    @NotNull
    @Override
    public Holder<String> PLAYER_NAME_TAG() {
        return PLAYER_NAME_TAG;
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
    public NumberProvider DEATH_GRAVE_SPAWN_SEARCH_RANGE() {
        return DEATH_GRAVE_SPAWN_SEARCH_RANGE.value();
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

    @NotNull
    @Override
    public NumberProvider DEATH_EXPIRY_TIME() {
        return DEATH_EXPIRY_TIME.value();
    }

    @NotNull
    @Override
    public Holder<Component> SERVER_MOTD() {
        return SERVER_MOTD;
    }

    @NotNull
    @Override
    public Holder<ResourcePackRequestData> RESOURCE_PACKS() {
        return RESOURCE_PACKS;
    }

    @NotNull
    @Override
    public NumberProvider RESOURCE_OVERWORLD_BORDER() {
        return RESOURCE_OVERWORLD_BORDER.value();
    }

    @NotNull
    @Override
    public NumberProvider RESOURCE_NETHER_BORDER() {
        return RESOURCE_NETHER_BORDER.value();
    }

    @NotNull
    @Override
    public NumberProvider RESOURCE_OVERWORLD_RESET_TIME() {
        return RESOURCE_OVERWORLD_RESET_TIME.value();
    }

    @NotNull
    @Override
    public NumberProvider TPA_EXPIRE_TICKS() {
        return TPA_EXPIRE_TICKS.value();
    }

    @NotNull
    @Override
    public NumberProvider TPA_CONFIRM_EXPIRE_TICKS() {
        return TPA_CONFIRM_EXPIRE_TICKS.value();
    }

    @NotNull
    @Override
    public Holder<String> TPA_TELEPORT_COMPONENTS() {
        return TPA_TELEPORT_COMPONENTS;
    }

    @NotNull
    @Override
    public NumberProvider UPDATE_TAB_TIME() {
        return UPDATE_TAB_TIME.value();
    }

    @NotNull
    @Override
    public Holder<String> TAB_HEADER() {
        return TAB_HEADER;
    }

    @NotNull
    @Override
    public Holder<String> TAB_FOOTER() {
        return TAB_FOOTER;
    }

    @NotNull
    @Override
    public Holder<List<String>> INFUSED_POTIONS_TAG_FORMAT() {
        return INFUSED_POTIONS_TAG_FORMAT;
    }

    @NotNull
    @Override
    public Holder<List<String>> INFUSED_POTIONS_EFFECTS_LIST_TAG_FORMAT() {
        return INFUSED_POTIONS_EFFECTS_LIST_TAG_FORMAT;
    }

    @NotNull
    @Override
    public Holder<ItemPredicate> APPLICABLE_INFUSION_BENEFITS() {
        return APPLICABLE_INFUSION_BENEFITS;
    }

    @NotNull
    @Override
    public NumberProvider INFUSION_DURABILITY_PERCENT() {
        return INFUSION_DURABILITY_PERCENT.value();
    }

    @NotNull
    @Override
    public Holder<Map<Key, Double>> INFUSION_ATTRIBUTE_BENEFITS() {
        return INFUSION_ATTRIBUTE_BENEFITS;
    }

    @NotNull
    @Override
    public NumberProvider SLEEPER_WORLD_TIME_SKIP() {
        return SLEEPER_WORLD_TIME_SKIP.value();
    }

    @Override
    public NumberProvider SLEEP_PREVENT_IF_ATTACKED_WITHIN() {
        return SLEEP_PREVENT_IF_ATTACKED_WITHIN.value();
    }

    @Override
    public Holder<Collection<CfgLootData>> CHESTS_LOOT_DATA() {
        return CHESTS_LOOT_DATA;
    }

    @Override
    public Holder<Collection<CfgLootData>> BLOCK_DISPENSE_LOOT_DATA() {
        return BLOCK_DISPENSE_LOOT_DATA;
    }
}
