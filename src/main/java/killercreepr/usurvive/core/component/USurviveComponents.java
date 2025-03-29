package killercreepr.usurvive.core.component;

import killercreepr.crux.api.component.DataComponentType;
import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxteleport.api.component.TeleporterComponent;
import killercreepr.usurvive.api.component.InfusedPotions;
import killercreepr.usurvive.api.component.PotionInfusable;
import killercreepr.usurvive.api.component.WaterProjectile;
import killercreepr.usurvive.api.mechanics.grapple.Grapple;
import killercreepr.usurvive.api.mechanics.whip.Whip;
import killercreepr.usurvive.core.block.GlintingOreBlockComponent;
import killercreepr.usurvive.core.component.impl.InfusionAttributesBenefit;
import killercreepr.usurvive.core.component.impl.OrbitConduitComponent;
import killercreepr.usurvive.core.component.impl.SmokeParticleBlockComponent;
import killercreepr.usurvive.core.component.impl.SoulParticleBlockComponent;
import killercreepr.usurvive.core.component.teleport.TeleportCheckInStructure;

import java.util.function.UnaryOperator;

public class USurviveComponents {
    public static void register(){};
    public static final DataComponentType<Grapple> GRAPPLE = register("grapple", builder -> builder
        .persistTextParser(USurviveCompParsers.GRAPPLE.createInput(Crux.key("grapple"))));
    public static final DataComponentType<Grapple> GRAPPLE_DISPLAY = register("grapple_display", builder -> builder
        .persistTextParser(USurviveCompParsers.GRAPPLE_DISPLAY.createInput(Crux.key("grapple_display"))));
    public static final DataComponentType<Whip> WHIP_DISPLAY = register("whip_display", builder -> builder
        .persistTextParser(USurviveCompParsers.WHIP_DISPLAY.createInput(Crux.key("whip_display"))));
    public static final DataComponentType<OrbitConduitComponent> DEPOSIT_CONDUIT = register("deposit_conduit", builder ->
        builder);
    public static final DataComponentType<SoulParticleBlockComponent> SOUL_PARTICLE_BLOCK = register("soul_particle_block", builder ->
        builder);
    public static final DataComponentType<SmokeParticleBlockComponent> SMOKE_PARTICLE_BLOCK = register("smoke_particle_block", builder ->
        builder);
    public static final DataComponentType<GlintingOreBlockComponent> GLINTING_ORE_BLOCK = register("glinting_ore_block", builder ->
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


    public static final DataComponentType<TeleportCheckInStructure> TELEPORT_STRUCTURE_CHECK = register("teleport/structure_check", builder ->
        builder.persistTextParser(USurviveCompParsers.TELEPORT_STRUCTURE_CHECK.createInput(Crux.key("teleport/structure_check"))));

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}