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
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.data.ResourcePackRequestData;
import killercreepr.usurvive.core.values.DefaultValues;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

@killercreepr.cruxconfig.config.common.annotations.Config(autoUpdate = true)
public class Config extends Cfg implements ValuesProvider {
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
}
