package killercreepr.usurvive.core.component;

import killercreepr.crux.api.component.DataComponentType;
import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.parser.type.ComponentInputParsers;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxattributes.core.component.CruxAttributeCompParsers;
import killercreepr.cruxteleport.api.component.TeleporterComponent;
import killercreepr.usurvive.api.component.InfusedPotions;
import killercreepr.usurvive.api.component.PotionInfusable;
import killercreepr.usurvive.api.component.WaterProjectile;
import killercreepr.usurvive.api.mechanics.grapple.Grapple;
import killercreepr.usurvive.api.mechanics.whip.Whip;
import killercreepr.usurvive.core.block.GlintingOreBlockComponent;
import killercreepr.usurvive.core.component.impl.*;
import killercreepr.usurvive.core.component.teleport.TeleportCheckInStructure;
import killercreepr.usurvive.core.component.teleport.TeleportSetLastTP;
import killercreepr.usurvive.core.mechanics.*;
import net.kyori.adventure.key.Key;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class USurviveComponents {
    public static void register(){};
    public static final DataComponentType<Grapple> GRAPPLE = register("grapple", builder -> builder
        .persistTextParser(USurviveCompParsers.GRAPPLE.createInput(Crux.key("grapple"))));
    public static final DataComponentType<Boolean> WAXED = register("waxed", builder -> builder
        .persistTextParser(PersistTextParser.BOOLEAN.createInput(Crux.key("waxed"))));
    public static final DataComponentType<Grapple> GRAPPLE_DISPLAY = register("grapple_display", builder -> builder
        .persistTextParser(USurviveCompParsers.GRAPPLE_DISPLAY.createInput(Crux.key("grapple_display"))));
    public static final DataComponentType<Whip> WHIP_DISPLAY = register("whip_display", builder -> builder
        .persistTextParser(USurviveCompParsers.WHIP_DISPLAY.createInput(Crux.key("whip_display"))));
    public static final DataComponentType<OrbitConduitComponent> DEPOSIT_CONDUIT = register("deposit_conduit", builder ->
        builder);
    public static final DataComponentType<GustTNTComponent> GUST_TNT = register("gust_tnt", builder ->
        builder);
    public static final DataComponentType<SoulParticleBlockComponent> SOUL_PARTICLE_BLOCK = register("soul_particle_block", builder ->
        builder);
    public static final DataComponentType<SmokeParticleBlockComponent> SMOKE_PARTICLE_BLOCK = register("smoke_particle_block", builder ->
        builder);
    public static final DataComponentType<GlintingOreBlockComponent> GLINTING_ORE_BLOCK = register("glinting_ore_block", builder ->
        builder);
    public static final DataComponentType<WatcherPillarComponent> WATCHER_PILLAR_BLOCK = register("watcher_pillar_block", builder ->
        builder);
    public static final DataComponentType<TeleporterComponent> TPA_CHECK_WORLD = register("teleport/tpa_check_world", builder ->
        builder);
    public static final DataComponentType<InfusedPotions> INFUSED_POTIONS = register("infused_potions", builder ->
        builder.persistTextParser(USurviveCompParsers.INFUSED_POTIONS.createInput(Crux.key("infused_potions"))));
    public static final DataComponentType<PotionInfusable> POTION_INFUSABLE = register("potion_infusable", builder ->
        builder.persistTextParser(USurviveCompParsers.POTION_INFUSABLE.createInput(Crux.key("potion_infusable"))));

    public static final DataComponentType<Integer> POTION_INFUSION_DURABILITY_BENEFIT = register("potion_infusion_durability_benefit", builder ->
        builder.persistTextParser(PersistTextParser.INTEGER.createInput(Crux.key("potion_infusion_durability_benefit"))));
    public static final DataComponentType<InfusionAttributesBenefit> POTION_INFUSION_ATTRIBUTES_BENEFIT = register("potion_infusion_attributes_benefit", builder ->
        builder.persistTextParser(USurviveCompParsers.INFUSION_ATTRIBUTES_BENEFIT.createInput(Crux.key("potion_infusion_attributes_benefit"))));

    public static final DataComponentType<WaterProjectile> WATER_PROJECTILE = register("water_projectile", builder ->
        builder.persistTextParser(USurviveCompParsers.WATER_PROJECTILE.createInput(Crux.key("water_projectile"))));
    public static final DataComponentType<BaseItemUpgradedFrom> BASE_ITEM_UPGRADED_FROM = register("base_item_upgraded_from", builder ->
        builder.persistTextParser(USurviveCompParsers.BASE_ITEM_UPGRADED_FROM.createInput(Crux.key("base_item_upgraded_from"))));


    public static final DataComponentType<TeleportCheckInStructure> TELEPORT_STRUCTURE_CHECK = register("teleport/structure_check", builder ->
        builder.persistTextParser(USurviveCompParsers.TELEPORT_STRUCTURE_CHECK.createInput(Crux.key("teleport/structure_check"))));

    public static final DataComponentType<TeleportSetLastTP> TELEPORT_SET_LAST_TP = register("teleport/set_last_tp", builder ->
        builder.persistTextParser(USurviveCompParsers.TELEPORT_SET_LAST_TP.createInput(Crux.key("teleport/set_last_tp"))));

    public static final DataComponentType<Long> WORLD_RANDOM_TELEPORT_TIME = register("world/random_teleport_time", builder ->
        builder.persistTextParser(PersistTextParser.LONG.createInput(Crux.key("world/random_teleport_time"))));

    public static final DataComponentType<StickyChargeComponent> STICKY_CHARGE = register("sticky_charge", builder ->
        builder.persistTextParser(USurviveCompParsers.STICKY_CHARGE.createInput(Crux.key("sticky_charge"))));

    public static final DataComponentType<LashComponent> LASH = register("lash", builder ->
        builder.persistTextParser(USurviveCompParsers.LASH.createInput(Crux.key("lash"))));

    public static final DataComponentType<List<PotionEffect>> PROJECTILE_HIT_POTIONS = register("projectile_hit_potions", builder ->
        builder.persistTextParser(ComponentInputParsers.LIST.POTION_EFFECT.createInput(Crux.key("projectile_hit_potions"))));

    public static final DataComponentType<ThrowableEffectsComponent> THROWABLE_EFFECTS = register("throwable_effects", builder ->
        builder.persistTextParser(USurviveCompParsers.THROWABLE_EFFECTS.createInput(Crux.key("throwable_effects"))));

    public static final DataComponentType<CruxAttributeContainer> PROJECTILE_ATTRIBUTES = register("projectile_attributes", builder ->
        builder.persistTextParser(CruxAttributeCompParsers.CRUX_ATTRIBUTE_CONTAINER.createInput(Crux.key("projectile_attributes"))));

    public static final DataComponentType<GloomstingComponent> GLOOMSTING = register("gloomsting", builder ->
        builder.persistTextParser(USurviveCompParsers.GLOOMSTING.createInput(Crux.key("gloomsting"))));

    public static final DataComponentType<Map<Key, Double>> EXTRA_MOB_TYPE_DAMAGE = register("extra_mob_type_damage", builder ->
        builder.persistTextParser(USurviveCompParsers.EXTRA_MOB_TYPE_DAMAGE.createInput(Crux.key("extra_mob_type_damage"))));

    public static final DataComponentType<Map<Key, Double>> PROJECTILE_EXTRA_MOB_TYPE_DAMAGE = register("projectile_extra_mob_type_damage", builder ->
        builder.persistTextParser(USurviveCompParsers.EXTRA_MOB_TYPE_DAMAGE.createInput(Crux.key("projectile_extra_mob_type_damage"))));

    public static final DataComponentType<AttackPotionEffectsComponent> ATTACK_POTION_EFFECTS = register("attack_potion_effects", builder ->
        builder.persistTextParser(USurviveCompParsers.ATTACK_POTION_EFFECTS.createInput(Crux.key("attack_potion_effects"))));


    public static final DataComponentType<FungireRepairComponent> FUNGIRE_REPAIR = register("fungire_repair", builder ->
        builder.persistTextParser(USurviveCompParsers.FUNGIRE_REPAIR.createInput(Crux.key("fungire_repair"))));

    public static final DataComponentType<GlowingMeadComponent> GLOWING_MEAD = register("glowing_mead", builder ->
        builder.persistTextParser(USurviveCompParsers.GLOWING_MEAD.createInput(Crux.key("glowing_mead"))));

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}