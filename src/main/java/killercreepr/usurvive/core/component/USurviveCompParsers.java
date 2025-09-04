package killercreepr.usurvive.core.component;

import killercreepr.crux.api.component.parser.InputDecodeContext;
import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.api.component.parser.hybrid.TextInputField;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.component.parser.type.ComponentInputParsers;
import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import killercreepr.cruxattributes.core.component.CruxAttributeCompParsers;
import killercreepr.cruxpotions.core.component.parser.PotionCompParsers;
import killercreepr.usurvive.api.component.InfusedPotionGroup;
import killercreepr.usurvive.api.component.InfusedPotions;
import killercreepr.usurvive.api.component.PotionInfusable;
import killercreepr.usurvive.api.component.WaterProjectile;
import killercreepr.usurvive.api.mechanics.grapple.Grapple;
import killercreepr.usurvive.api.mechanics.whip.Whip;
import killercreepr.usurvive.core.component.impl.*;
import killercreepr.usurvive.core.component.teleport.TeleportCheckInStructure;
import killercreepr.usurvive.core.component.teleport.TeleportSetLastTP;
import killercreepr.usurvive.core.mechanics.*;
import killercreepr.usurvive.core.mechanics.bone_flute.FluteSoundSequence;
import killercreepr.usurvive.core.mechanics.grapple.SimpleDisplayGrapple;
import killercreepr.usurvive.core.mechanics.grapple.SimpleGrapple;
import killercreepr.usurvive.core.mechanics.whip.SimpleDisplayWhip;
import net.kyori.adventure.key.Key;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

public class USurviveCompParsers {
    public static final PersistTextParser<Grapple> GRAPPLE = PersistTextParser.mapBuilder(Grapple.class)
        .field("grapple_type", TextInputField.field(PersistTextParser.KEY, Grapple::getHookType))
        .field("max_hooks", TextInputField.field(PersistTextParser.INTEGER, Grapple::getMaxHooks))
        .field("throw_force", TextInputField.field(PersistTextParser.FLOAT, Grapple::getThrowForce))
        .field("pull_force", TextInputField.field(PersistTextParser.FLOAT, Grapple::getPullForce))
        .field("pull_decay_rate", TextInputField.field(PersistTextParser.FLOAT, Grapple::getPullForceDecayRate))
        .field("min_pull_multiplier", TextInputField.field(PersistTextParser.FLOAT, Grapple::getMinPullForceMultiplier))
        .field("decay_duration", TextInputField.field(PersistTextParser.INTEGER, Grapple::getDecayDuration))
        .field("cooldown", TextInputField.field(PersistTextParser.INTEGER, Grapple::getCooldown))

        .field("swing_force", TextInputField.field(PersistTextParser.FLOAT, Grapple::getPullForce))
        .field("tension_strength", TextInputField.field(PersistTextParser.FLOAT, Grapple::getPullForceDecayRate))
        .field("max_rope_length", TextInputField.field(PersistTextParser.FLOAT, Grapple::getMinPullForceMultiplier))
        .field("swing_boost_speed", TextInputField.field(PersistTextParser.FLOAT, Grapple::getSwingBoostSpeed))
        .field("rope_boost_speed", TextInputField.field(PersistTextParser.FLOAT, Grapple::getRopeBoostSpeed))

        .field("item_count_use", TextInputField.field(PersistTextParser.INTEGER, Grapple::getItemCountUse))
        .field("swap_hands_climb_speed", TextInputField.field(PersistTextParser.FLOAT, Grapple::getSwapHandsClimbSpeed))
        .apply(ctx ->{
            Key grappleType = ctx.get("grapple_type");
            int maxHooks = ctx.getOptional("max_hooks", 1);
            int cooldown = ctx.getOptional("cooldown", 2);
            float pullForce = ctx.getOptional("pull_force", ctx.getOptional("swing_force", .4f));//temp change?
            float throwForce = ctx.getOptional("throw_force", 1f);


            return new SimpleGrapple(
                grappleType, maxHooks, cooldown, pullForce, throwForce,

                ctx.getOptional("pull_decay_rate", ctx.getOptional("tension_strength",1f)), //temp change?
                ctx.getOptional("min_pull_multiplier", ctx.getOptional("max_rope_length", .2f)), //temp change?
                ctx.getOptional("decay_duration", 20),

                ctx.getOptional("swing_boost_speed", 50f),
                ctx.getOptional("rope_boost_speed", 2f),

                ctx.getOptional("item_count_use", 0),
                ctx.getOptional("swap_hands_climb_speed", 0f)
            );
        });

    public static final PersistTextParser<Grapple> GRAPPLE_DISPLAY = PersistTextParser.mapBuilder(Grapple.class)
        .field("grapple_type", TextInputField.field(PersistTextParser.KEY, Grapple::getHookType))
        .field("max_hooks", TextInputField.field(PersistTextParser.INTEGER, Grapple::getMaxHooks))
        .field("cooldown", TextInputField.field(PersistTextParser.INTEGER, Grapple::getCooldown))
        .field("throw_force", TextInputField.field(PersistTextParser.FLOAT, Grapple::getThrowForce))
        .field("pull_force", TextInputField.field(PersistTextParser.FLOAT, Grapple::getPullForce))
        .field("swing_force", TextInputField.field(PersistTextParser.FLOAT, Grapple::getPullForce))
        .field("tension_strength", TextInputField.field(PersistTextParser.FLOAT, Grapple::getPullForceDecayRate))
        .field("max_rope_length", TextInputField.field(PersistTextParser.FLOAT, Grapple::getMinPullForceMultiplier))
        .field("pull_decay_rate", TextInputField.field(PersistTextParser.FLOAT, Grapple::getPullForceDecayRate))
        .field("min_pull_multiplier", TextInputField.field(PersistTextParser.FLOAT, Grapple::getMinPullForceMultiplier))
        .field("decay_duration", TextInputField.field(PersistTextParser.INTEGER, Grapple::getDecayDuration))

        .field("type_key", TextInputField.field(PersistTextParser.KEY, e -> ((SimpleDisplayGrapple) e).getTypeKey()))
        .field("display_type", TextInputField.field(PersistTextParser.STRING, e -> ((SimpleDisplayGrapple) e).getDisplayType()))
        .field("range", TextInputField.field(PersistTextParser.INTEGER, e -> ((SimpleDisplayGrapple) e).getMaxDistance()))
        .field("retract_speed", TextInputField.field(PersistTextParser.FLOAT, e -> ((SimpleDisplayGrapple) e).getRetractSpeed()))
        .field("swing_boost_speed", TextInputField.field(PersistTextParser.FLOAT, Grapple::getSwingBoostSpeed))
        .field("rope_boost_speed", TextInputField.field(PersistTextParser.FLOAT, Grapple::getRopeBoostSpeed))

        .field("item_count_use", TextInputField.field(PersistTextParser.INTEGER, Grapple::getItemCountUse))
        .field("swap_hands_climb_speed", TextInputField.field(PersistTextParser.FLOAT, Grapple::getSwapHandsClimbSpeed))
        .apply(ctx ->{
            Key grappleType = ctx.get("grapple_type");
            int maxHooks = ctx.getOptional("max_hooks", 1);
            int cooldown = ctx.getOptional("cooldown", 2);
            float pullForce = ctx.getOptional("pull_force", ctx.getOptional("swing_force", .4f));//temp change?
            float throwForce = ctx.getOptional("throw_force", .8f);

            return new SimpleDisplayGrapple(
                grappleType, maxHooks, cooldown, pullForce, throwForce,

                ctx.getOptional("pull_decay_rate", ctx.getOptional("tension_strength",1f)), //temp change?
                ctx.getOptional("min_pull_multiplier", ctx.getOptional("max_rope_length", .2f)), //temp change?
                ctx.getOptional("decay_duration", 20),

                ctx.getOptional("swing_boost_speed", 50f),
                ctx.getOptional("rope_boost_speed", 2f),

                ctx.getOptional("item_count_use", 0),
                ctx.getOptional("swap_hands_climb_speed", 0f),

                ctx.get("type_key"),
                ctx.getOptional("display_type", "block"),
                ctx.getOptional("range", 16),
                ctx.getOptional("retract_speed", .5f)
            );
        });

    public static final PersistTextParser<Whip> WHIP_DISPLAY = PersistTextParser.mapBuilder(Whip.class)
        .field("whip_type", TextInputField.field(PersistTextParser.KEY, Whip::getWhipType))
        .field("max_hooks", TextInputField.field(PersistTextParser.INTEGER, Whip::getMaxWhips))
        .field("item_damage", TextInputField.field(PersistTextParser.INTEGER, Whip::getItemDamage))
        .field("throw_sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND, Whip::getThrowSound))
        .field("cooldown", TextInputField.field(PersistTextParser.INTEGER, Whip::getCooldown))

        .field("type_key", TextInputField.field(PersistTextParser.KEY, e -> ((SimpleDisplayWhip) e).getTypeKey()))
        .field("display_type", TextInputField.field(PersistTextParser.STRING, e -> ((SimpleDisplayWhip) e).getDisplayType()))
        .apply(ctx ->{
            Key whipType = ctx.get("whip_type");

            int maxHooks = ctx.getOptional("max_hooks", 1);
            int cooldown = ctx.getOptional("cooldown", 2);
            int itemDamage = ctx.getOptional("item_damage", 1);

            return new SimpleDisplayWhip(
                whipType, maxHooks, cooldown, itemDamage,
                ctx.getOptional("throw_sound"),
                ctx.get("type_key"),
                ctx.getOptional("display_type", "block")
            );
        });

    public static final PersistTextParser<InfusedPotionGroup> INFUSED_POTION_GROUP = PersistTextParser.mapBuilder(InfusedPotionGroup.class)
        .field("potions", TextInputField.field(ComponentInputParsers.LIST.POTION_EFFECT, InfusedPotionGroup::getPotionEffects))
        .field("slot", TextInputField.field(CruxAttributeCompParsers.CRUX_SLOT_GROUP, InfusedPotionGroup::getSlot))
        .apply(ctx ->{
            List<PotionEffect> potions = ctx.get("potions");
            CruxSlotGroup slot = ctx.getOptional("slot", CruxSlotGroup.ANY);
            return new SimpleInfusedPotionGroup(potions, slot);
        });

    public static final PersistTextParser<InfusedPotions> INFUSED_POTIONS = PersistTextParser.mapBuilder(InfusedPotions.class)
        .field("infusion", TextInputField.field(PersistTextParser.list(INFUSED_POTION_GROUP), InfusedPotions::getInfusedPotions))
        .apply(ctx ->{
            List<InfusedPotionGroup> infusion = ctx.getOptional("infusion", List.of());
            return new SimpleInfusedPotions(infusion);
        });

    public static final PersistTextParser<PotionInfusable> POTION_INFUSABLE = PersistTextParser.mapBuilder(PotionInfusable.class)
        .field("max_infusions", TextInputField.field(PersistTextParser.INTEGER, PotionInfusable::getMaxInfusions))
        .apply(ctx ->{
            int maxInfusions = ctx.getOptional("max_infusions", 3);
            return new SimplePotionInfusable(maxInfusions);
        });

    public static final PersistTextParser<InfusionAttributesBenefit> INFUSION_ATTRIBUTES_BENEFIT = PersistTextParser.mapBuilder(InfusionAttributesBenefit.class)
        .field("attributes", TextInputField.field(ComponentInputParsers.MAP.KEY_DOUBLE_MAP, InfusionAttributesBenefit::getAttributes))
        .field("keep_defaults", TextInputField.field(PersistTextParser.BOOLEAN, InfusionAttributesBenefit::isKeepDefaults))
        .apply(ctx -> new InfusionAttributesBenefit(ctx.get("attributes"), ctx.getOptional("keep_defaults")));

    public static final PersistTextParser<WaterProjectile> WATER_PROJECTILE = PersistTextParser.mapBuilder(WaterProjectile.class)
        .field("force", TextInputField.field(PersistTextParser.DOUBLE, WaterProjectile::force))
        .field("drop_off_rate", TextInputField.field(PersistTextParser.DOUBLE, WaterProjectile::dropOffRate))
        .field("max_time", TextInputField.field(PersistTextParser.INTEGER, WaterProjectile::maxTime))
        .apply(ctx -> new SimpleWaterProjectile(
            ctx.getOptional("force", .05D),
            ctx.getOptional("drop_off_rate", 2D),
            ctx.getOptional("max_time", 50)
        ));
    public static final PersistTextParser<BaseItemUpgradedFrom> BASE_ITEM_UPGRADED_FROM = PersistTextParser.mapBuilder(BaseItemUpgradedFrom.class)
        .field("type", TextInputField.field(PersistTextParser.STRING, BaseItemUpgradedFrom::getType))
        .field("color", TextInputField.field(PersistTextParser.COLOR, BaseItemUpgradedFrom::getColor))
        .apply(ctx -> new BaseItemUpgradedFrom(
            ctx.get("type"),
            ctx.get("color")
        ));

    public static final PersistTextParser<TeleportCheckInStructure> TELEPORT_STRUCTURE_CHECK = PersistTextParser.mapBuilder(TeleportCheckInStructure.class)
        .field("blacklist", TextInputField.field(ComponentInputParsers.KEY_PREDICATE, TeleportCheckInStructure::getBlacklist))
        .apply(ctx -> new TeleportCheckInStructure(
            ctx.getOptional("blacklist")
        ));

    public static final PersistTextParser<TeleportSetLastTP> TELEPORT_SET_LAST_TP = PersistTextParser.mapBuilder(TeleportSetLastTP.class)
        .apply(ctx -> new TeleportSetLastTP());

    public static final PersistTextParser<StickyChargeComponent> STICKY_CHARGE = PersistTextParser.mapBuilder(StickyChargeComponent.class)
        .field("throw_force", TextInputField.field(PersistTextParser.FLOAT, StickyChargeComponent::getThrowForce))
        .field("fuse_ticks", TextInputField.field(PersistTextParser.INTEGER, StickyChargeComponent::getFuseTicks))
        .field("attributes", TextInputField.field(CruxAttributeCompParsers.CRUX_ATTRIBUTE_CONTAINER, StickyChargeComponent::getAttributes))
        .field("crux_potions", TextInputField.field(PotionCompParsers.LIST_STORED_CRUX_POTION, StickyChargeComponent::getCruxPotions))
        .apply(ctx -> new StickyChargeComponent(
            ctx.getOptional("throw_force", 1f),
            ctx.getOptional("attributes"),
            ctx.getOptional("fuse_ticks", 60),
            ctx.getOptional("crux_potions")
        ));

    public static final PersistTextParser<LashComponent> LASH = PersistTextParser.mapBuilder(LashComponent.class)
        .field("cooldown", TextInputField.field(PersistTextParser.INTEGER, LashComponent::getCooldown))
        .field("item_damage", TextInputField.field(PersistTextParser.BYTE, LashComponent::getItemDamage))
        .field("push_force", TextInputField.field(PersistTextParser.FLOAT, LashComponent::getPushForce))
        .field("push_force_y", TextInputField.field(PersistTextParser.FLOAT, LashComponent::getPushForceY))
        .field("use_sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND, LashComponent::getUseSound))
        .apply(ctx -> new LashComponent(
            ctx.getOptional("cooldown", 0),
            ctx.getOptional("item_damage", (byte)0),
            ctx.getOptional("push_force", 0f),
            ctx.getOptional("push_force_y", 0f),
            ctx.getOptional("use_sound")
        ));

    public static final PersistTextParser<ThrowableEffectsComponent> THROWABLE_EFFECTS = PersistTextParser.mapBuilder(ThrowableEffectsComponent.class)
        .field("crux_potions", TextInputField.field(PotionCompParsers.LIST_STORED_CRUX_POTION, ThrowableEffectsComponent::getCruxPotions))
        .field("potions", TextInputField.field(ComponentInputParsers.LIST.POTION_EFFECT, ThrowableEffectsComponent::getPotions))
        .field("throw_sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND, ThrowableEffectsComponent::getThrowSound))
        .field("throw_force", TextInputField.field(PersistTextParser.FLOAT, ThrowableEffectsComponent::getThrowForce))
        .field("spread", TextInputField.field(PersistTextParser.FLOAT, ThrowableEffectsComponent::getSpread))
        .apply(ctx -> new ThrowableEffectsComponent(
            ctx.getOptional("crux_potions"),
            ctx.getOptional("potions"),
            ctx.getOptional("throw_sound"),
            ctx.getOptional("throw_force", 1f),
            ctx.getOptional("spread", .1f)
        ));

    public static final PersistTextParser<GloomstingComponent> GLOOMSTING = PersistTextParser.mapBuilder(GloomstingComponent.class)
        .field("attributes", TextInputField.field(CruxAttributeCompParsers.CRUX_ATTRIBUTE_CONTAINER, GloomstingComponent::getAttributes))
        .field("use_sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND, GloomstingComponent::getUseSound))
        .field("shoot_force", TextInputField.field(PersistTextParser.FLOAT, GloomstingComponent::getShootForce))
        .field("spread", TextInputField.field(PersistTextParser.FLOAT, GloomstingComponent::getSpread))
        .field("cooldown", TextInputField.field(PersistTextParser.INTEGER, GloomstingComponent::getCooldown))
        .field("item_damage", TextInputField.field(PersistTextParser.INTEGER, GloomstingComponent::getItemDamage))
        .field("shoot_amount", TextInputField.field(ComponentInputParsers.NUMBER_PROVIDER, GloomstingComponent::getShootAmount))
        .field("crux_potions", TextInputField.field(PotionCompParsers.LIST_STORED_CRUX_POTION, GloomstingComponent::getCruxPotions))
        .field("potions", TextInputField.field(ComponentInputParsers.LIST.POTION_EFFECT, GloomstingComponent::getPotions))
        .apply(ctx -> new GloomstingComponent(
            ctx.getOptional("cooldown"),
            ctx.getOptional("item_damage"),
            ctx.getOptional("shoot_force"),
            ctx.getOptional("spread"),
            ctx.getOptional("shoot_amount"),
            ctx.getOptional("attributes"),
            ctx.getOptional("use_sound"),
            ctx.getOptional("crux_potions"),
            ctx.getOptional("potions")
        ));

    public static final PersistTextParser<Map<Key, Double>> EXTRA_MOB_TYPE_DAMAGE = PersistTextParser.mapBuilder(Map.class)
        .field("", TextInputField.field(
            PersistTextParser.mapDynamicBuilder((Class<Map< Key, Double>>) (Class) Map.class)
                .keyParser(PersistTextParser.KEY)
                .valueParser(PersistTextParser.DOUBLE)
                .mapToEncode(m -> m)
                .apply(InputDecodeContext::get), e -> e
        )).build();

    public static final PersistTextParser<AttackPotionEffectsComponent.AttackPotionEffect> ATTACK_POTION_EFFECT = PersistTextParser.mapBuilder(AttackPotionEffectsComponent.AttackPotionEffect.class)
        .field("chance", TextInputField.field(PersistTextParser.FLOAT, AttackPotionEffectsComponent.AttackPotionEffect::getChance))
        .field("potions", TextInputField.field(ComponentInputParsers.LIST.POTION_EFFECT, AttackPotionEffectsComponent.AttackPotionEffect::getEffects))
        .apply(ctx -> new AttackPotionEffectsComponent.AttackPotionEffect(
            ctx.getOptional("potions"),
            ctx.getOptional("chance", 100f)
        ));

    public static final PersistTextParser<AttackPotionEffectsComponent> ATTACK_POTION_EFFECTS = PersistTextParser.mapBuilder(AttackPotionEffectsComponent.class)
        .field("effects", TextInputField.field(PersistTextParser.collectionList(ATTACK_POTION_EFFECT), AttackPotionEffectsComponent::getEffects))
        .apply(ctx -> new AttackPotionEffectsComponent(
            ctx.getOptional("effects")
        ));

    public static final PersistTextParser<FungireRepairComponent.WorldRepair> FUNGIRE_REPAIR_WORLD_REPAIR = PersistTextParser.mapBuilder(FungireRepairComponent.WorldRepair.class)
        .field("repair_chance", TextInputField.field(PersistTextParser.FLOAT, FungireRepairComponent.WorldRepair::repairChance))
        .field("repair_amount", TextInputField.field(ComponentInputParsers.NUMBER_PROVIDER, FungireRepairComponent.WorldRepair::repairAmount))
        .apply(ctx -> new FungireRepairComponent.WorldRepair(
            ctx.getOptional("repair_chance", 50f),
            ctx.getOptional("repair_amount", NumberProvider.constant(2))
        ));

    public static final PersistTextParser<FungireRepairComponent> FUNGIRE_REPAIR = PersistTextParser.mapBuilder(FungireRepairComponent.class)
        .field("repair_chance", TextInputField.field(PersistTextParser.FLOAT, FungireRepairComponent::getRepairChance))
        .field("repair_amount", TextInputField.field(ComponentInputParsers.NUMBER_PROVIDER, FungireRepairComponent::getRepairAmount))
        .field("exhaust_chance", TextInputField.field(PersistTextParser.FLOAT, FungireRepairComponent::getExhaustionChance))
        .field("exhaust_amount", TextInputField.field(ComponentInputParsers.NUMBER_PROVIDER, FungireRepairComponent::getExhaustionAmount))
        .field("world_repair", TextInputField.field(
            PersistTextParser.mapDynamicBuilder((Class<Map<Key, FungireRepairComponent.WorldRepair>>) (Class) Map.class)
                .keyParser(PersistTextParser.KEY)
                .valueParser(FUNGIRE_REPAIR_WORLD_REPAIR)
                .mapToEncode(m -> m)
                .apply(InputDecodeContext::get), FungireRepairComponent::getWorldRepair
        ))
        .apply(ctx -> new FungireRepairComponent(
            ctx.getOptional("repair_chance", 100f),
            ctx.getOptional("repair_amount", NumberProvider.constant(3)),
            ctx.getOptional("exhaust_chance", 50f),
            ctx.getOptional("exhaust_amount", NumberProvider.constant(0.2f)),
            ctx.getOptional("world_repair")
        ));

    public static final PersistTextParser<GlowingMeadComponent> GLOWING_MEAD = PersistTextParser.mapBuilder(GlowingMeadComponent.class)
        .field("range", TextInputField.field(PersistTextParser.FLOAT, GlowingMeadComponent::getRange))
        .field("near_potions", TextInputField.field(ComponentInputParsers.LIST.POTION_EFFECT, GlowingMeadComponent::getNearbyPotions))
        .field("crux_potions", TextInputField.field(PotionCompParsers.LIST_STORED_CRUX_POTION, GlowingMeadComponent::getCruxPotions))
        .apply(ctx -> new GlowingMeadComponent(
            ctx.getOptional("range", 12f),
            ctx.getOptional("near_potions"),
            ctx.getOptional("crux_potions")
        ));

    public static final PersistTextParser<ReducePotionDurationComponent.Value> REDUCE_POTION_VALUE = PersistTextParser.mapBuilder(ReducePotionDurationComponent.Value.class)
        .field("potion", TextInputField.field(ComponentInputParsers.POTION_EFFECT_TYPE, ReducePotionDurationComponent.Value::type))
        .field("remove_duration", TextInputField.field(PersistTextParser.INTEGER, ReducePotionDurationComponent.Value::removeDuration))
        .apply(ctx -> new ReducePotionDurationComponent.Value(
            ctx.get("potion"),
            ctx.get("remove_duration")
        ));

    public static final PersistTextParser<List<ReducePotionDurationComponent.Value>> REDUCE_POTION_VALUE_LIST = PersistTextParser.list(REDUCE_POTION_VALUE);

    public static final PersistTextParser<ReducePotionDurationComponent> REDUCE_POTION_DURATION = PersistTextParser.mapBuilder(ReducePotionDurationComponent.class)
        .field("remove_potions", TextInputField.field(REDUCE_POTION_VALUE_LIST, ReducePotionDurationComponent::getRemovePotions))
        .apply(ctx -> new ReducePotionDurationComponent(
            ctx.get("remove_potions")
        ));

    public static final PersistTextParser<ExecuteConsoleCommandsComponent> EXECUTE_CONSOLE_COMMANDS = PersistTextParser.mapBuilder(ExecuteConsoleCommandsComponent.class)
        .field("right_click_commands", TextInputField.field(PersistTextParser.LIST.STRING,ExecuteConsoleCommandsComponent::getCommands))
        .apply(ctx -> new ExecuteConsoleCommandsComponent(
            ctx.get("right_click_commands")
        ));

    public static final PersistTextParser<ConfettiCannonComponent> CONFETTI_CANNON = PersistTextParser.mapBuilder(ConfettiCannonComponent.class)
        .field("force", TextInputField.field(PersistTextParser.DOUBLE, ConfettiCannonComponent::getForce))
        .field("size", TextInputField.field(PersistTextParser.DOUBLE, ConfettiCannonComponent::getSize))
        .field("par_amount", TextInputField.field(PersistTextParser.INTEGER, ConfettiCannonComponent::getParAmount))
        .field("item_damage", TextInputField.field(PersistTextParser.INTEGER, ConfettiCannonComponent::getItemDamage))
        .field("particle", TextInputField.field(PersistTextParser.STRING, ConfettiCannonComponent::getParticle))
        .field("sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND, ConfettiCannonComponent::getSound))
        .field("aoe_min", TextInputField.field(PersistTextParser.DOUBLE, ConfettiCannonComponent::getMinAOE))
        .field("aoe_drop_off", TextInputField.field(PersistTextParser.DOUBLE, ConfettiCannonComponent::getAoeDropOff))
        .field("par_life_span", TextInputField.field(ComponentInputParsers.NUMBER_PROVIDER, ConfettiCannonComponent::getParLifeSpan))
        .field("attack_damage_drop_off", TextInputField.field(PersistTextParser.FLOAT, ConfettiCannonComponent::getDmgDropOff))
        .field("second_sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND, ConfettiCannonComponent::getSecondSound))
        .field("second_sound_delay", TextInputField.field(PersistTextParser.INTEGER, ConfettiCannonComponent::getSecondSoundDelay))
        .apply(ctx -> new ConfettiCannonComponent(
            ctx.getOptional("force", 1.5D),
            ctx.getOptional("size", 1D),
            ctx.getOptional("par_amount", 30),
            ctx.getOptional("item_damage", 1),
            ctx.getOptional("particle", "confetti"),
            ctx.getOptional("sound"),
            ctx.getOptional("second_sound"),
            ctx.getOptional("second_sound_delay", 1),
            ctx.getOptional("aoe_min", 0.1D),
            ctx.getOptional("aoe_drop_off", 0.1D),
            ctx.getOptional("attack_damage_drop_off", 1f),
            ctx.getOptional("par_life_span", NumberProvider.uniform(200, 190))
        ));

    public static final PersistTextParser<FluteSoundSequence.FluteSound> FLUTE_SOUND = PersistTextParser.mapBuilder(FluteSoundSequence.FluteSound.class)
        .field("sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND,FluteSoundSequence.FluteSound::sound))
        .field("wait_time", TextInputField.field(PersistTextParser.INTEGER, FluteSoundSequence.FluteSound::waitTime))
        .apply(ctx -> new FluteSoundSequence.FluteSound.Simple(
            ctx.get("sound"), ctx.getOptional("wait_time", 1)
        ));


    public static final PersistTextParser<List<FluteSoundSequence.FluteSound>> FLUTE_SOUND_LIST = PersistTextParser.list(FLUTE_SOUND);

    public static final PersistTextParser<FluteSoundSequence> FLUTE_SOUND_SEQUENCE = PersistTextParser.mapBuilder(FluteSoundSequence.class)
        .field("sounds", TextInputField.field(FLUTE_SOUND_LIST, FluteSoundSequence::sounds))
        .apply(ctx -> new FluteSoundSequence.Simple(
            ctx.get("sounds")
        ));

    public static final PersistTextParser<BoneFluteComponent.RayData> BONE_FLUTE_RAY_DATA = PersistTextParser.mapBuilder(BoneFluteComponent.RayData.class)
        .field("yaw", TextInputField.field(ComponentInputParsers.NUMBER_PROVIDER, BoneFluteComponent.RayData::getYaw))
        .field("pitch", TextInputField.field(ComponentInputParsers.NUMBER_PROVIDER, BoneFluteComponent.RayData::getPitch))
        .field("attributes", TextInputField.field(CruxAttributeCompParsers.CRUX_ATTRIBUTE_CONTAINER, BoneFluteComponent.RayData::getAttributes))
        .apply(ctx -> new BoneFluteComponent.RayData(
            ctx.getOptional("yaw"),
            ctx.getOptional("pitch"),
            ctx.getOptional("attributes")
        ));

    public static final PersistTextParser<List<BoneFluteComponent.RayData>> BONE_FLUTE_RAY_DATA_LIST = PersistTextParser.list(BONE_FLUTE_RAY_DATA);

    public static final PersistTextParser<BoneFluteComponent> BONE_FLUTE = PersistTextParser.mapBuilder(BoneFluteComponent.class)
        .field("sound_sequence", TextInputField.field(FLUTE_SOUND_SEQUENCE,BoneFluteComponent::getSequence))
        .field("rays", TextInputField.field(BONE_FLUTE_RAY_DATA_LIST,BoneFluteComponent::getRays))
        .field("item_damage", TextInputField.field(PersistTextParser.INTEGER,BoneFluteComponent::getItemDamage))
        .apply(ctx -> new BoneFluteComponent(
            ctx.getOptional("sound_sequence"),
            ctx.getOptional("rays"),
            ctx.getOptional("item_damage", 1)
        ));

    public static final PersistTextParser<BlastTempestComponent.RayData> BLAST_TEMPEST_RAY_DATA = PersistTextParser.mapBuilder(BlastTempestComponent.RayData.class)
        .field("circle_radius", TextInputField.field(PersistTextParser.DOUBLE, e -> e.circleRadius))
        .field("type", TextInputField.field(PersistTextParser.STRING, e -> e.type))
        .field("homing", TextInputField.field(PersistTextParser.BOOLEAN, e -> e.homing))
        .field("attributes", TextInputField.field(CruxAttributeCompParsers.CRUX_ATTRIBUTE_CONTAINER, BlastTempestComponent.RayData::getAttributes))
        .field("dupe_amount", TextInputField.field(PersistTextParser.BYTE, e -> e.dupeAmount))
        .apply(ctx -> new BlastTempestComponent.RayData(
            ctx.getOptional("attributes"),
            ctx.getOptional("type", "default"),
            ctx.getOptional("circle_radius", 2.5D),
            ctx.getOptional("homing", false),
            ctx.getOptional("dupe_amount", (byte)0)
        ));

    public static final PersistTextParser<List<BlastTempestComponent.RayData>> BLAST_TEMPEST_RAY_DATA_LIST = PersistTextParser.list(BLAST_TEMPEST_RAY_DATA);

    public static final PersistTextParser<BlastTempestComponent> TNT_TEMPEST = PersistTextParser.mapBuilder(BlastTempestComponent.class)
        .field("rays", TextInputField.field(BLAST_TEMPEST_RAY_DATA_LIST,BlastTempestComponent::getRays))
        .field("item_damage", TextInputField.field(PersistTextParser.INTEGER,BlastTempestComponent::getItemDamage))
        .field("range", TextInputField.field(PersistTextParser.DOUBLE,BlastTempestComponent::getRange))
        .field("yaw_degrees", TextInputField.field(PersistTextParser.DOUBLE,BlastTempestComponent::getYawDegrees))
        .field("pitch_degrees", TextInputField.field(PersistTextParser.DOUBLE,BlastTempestComponent::getPitchDegrees))
        .field("launch_sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND,BlastTempestComponent::getLaunchSound))
        .field("lock_on_sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND,BlastTempestComponent::getLockOnSound))
        .apply(ctx -> new BlastTempestComponent(
            ctx.getOptional("rays"),
            ctx.getOptional("range", 16D),
            ctx.getOptional("yaw_degrees", 50D),
            ctx.getOptional("pitch_degrees", 50D),
            ctx.getOptional("item_damage", 1),
            ctx.getOptional("launch_sound"),
            ctx.getOptional("lock_on_sound")
        ));

    public static final PersistTextParser<PartyBurstComponent> PARTY_BURST = PersistTextParser.mapBuilder(PartyBurstComponent.class)
        .field("force", TextInputField.field(PersistTextParser.DOUBLE, PartyBurstComponent::getForce))
        .field("size", TextInputField.field(PersistTextParser.DOUBLE, PartyBurstComponent::getSize))
        .field("par_amount", TextInputField.field(PersistTextParser.INTEGER, PartyBurstComponent::getParAmount))
        .field("item_damage", TextInputField.field(PersistTextParser.INTEGER, PartyBurstComponent::getItemDamage))
        .field("item_removal", TextInputField.field(PersistTextParser.INTEGER, PartyBurstComponent::getItemRemoval))
        .field("particle", TextInputField.field(PersistTextParser.STRING, PartyBurstComponent::getParticle))
        .field("sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND, PartyBurstComponent::getSound))
        .field("par_life_span", TextInputField.field(ComponentInputParsers.NUMBER_PROVIDER, PartyBurstComponent::getParLifeSpan))
        .field("second_sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND, PartyBurstComponent::getSecondSound))
        .field("second_sound_delay", TextInputField.field(PersistTextParser.INTEGER, PartyBurstComponent::getSecondSoundDelay))
        .field("attributes", TextInputField.field(CruxAttributeCompParsers.CRUX_ATTRIBUTE_CONTAINER, PartyBurstComponent::getAttributes))
        .field("vanilla_particles", TextInputField.field(ComponentInputParsers.PARTICLE_BUILDER_SUPPLIER, PartyBurstComponent::getVanillaParticle))
        .apply(ctx -> new PartyBurstComponent(
            ctx.getOptional("force", 1.5D),
            ctx.getOptional("size", 1D),
            ctx.getOptional("par_amount", 30),
            ctx.getOptional("item_damage", 0),
            ctx.getOptional("item_removal", 1),
            ctx.getOptional("particle", "confetti"),
            ctx.getOptional("sound"),
            ctx.getOptional("second_sound"),
            ctx.getOptional("second_sound_delay", 1),
            ctx.getOptional("par_life_span", NumberProvider.uniform(200, 190)),
            ctx.get("attributes"),
            ctx.get("vanilla_particles")
        ));
}
