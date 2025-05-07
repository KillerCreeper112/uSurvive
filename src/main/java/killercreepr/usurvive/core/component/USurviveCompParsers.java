package killercreepr.usurvive.core.component;

import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.api.component.parser.hybrid.TextInputField;
import killercreepr.crux.core.component.parser.type.ComponentInputParsers;
import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import killercreepr.cruxattributes.core.component.CruxAttributeCompParsers;
import killercreepr.usurvive.api.component.InfusedPotionGroup;
import killercreepr.usurvive.api.component.InfusedPotions;
import killercreepr.usurvive.api.component.PotionInfusable;
import killercreepr.usurvive.api.component.WaterProjectile;
import killercreepr.usurvive.api.mechanics.grapple.Grapple;
import killercreepr.usurvive.api.mechanics.whip.Whip;
import killercreepr.usurvive.core.component.impl.*;
import killercreepr.usurvive.core.component.teleport.TeleportCheckInStructure;
import killercreepr.usurvive.core.component.teleport.TeleportSetLastTP;
import killercreepr.usurvive.core.mechanics.grapple.SimpleDisplayGrapple;
import killercreepr.usurvive.core.mechanics.grapple.SimpleGrapple;
import killercreepr.usurvive.core.mechanics.whip.SimpleDisplayWhip;
import net.kyori.adventure.key.Key;
import org.bukkit.potion.PotionEffect;

import java.util.List;

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
        .apply(ctx ->{
            Key grappleType = ctx.get("grapple_type");
            int maxHooks = ctx.getOptional("max_hooks", 1);
            int cooldown = ctx.getOptional("cooldown", 2);
            float pullForce = ctx.getOptional("pull_force", .4f);
            float throwForce = ctx.getOptional("throw_force", 1f);
            return new SimpleGrapple(
                grappleType, maxHooks, cooldown, pullForce, throwForce,

                ctx.getOptional("pull_decay_rate", 1f),
                ctx.getOptional("min_pull_multiplier", .2f),
                ctx.getOptional("decay_duration", 20)
            );
        });

    public static final PersistTextParser<Grapple> GRAPPLE_DISPLAY = PersistTextParser.mapBuilder(Grapple.class)
        .field("grapple_type", TextInputField.field(PersistTextParser.KEY, Grapple::getHookType))
        .field("max_hooks", TextInputField.field(PersistTextParser.INTEGER, Grapple::getMaxHooks))
        .field("cooldown", TextInputField.field(PersistTextParser.INTEGER, Grapple::getCooldown))
        .field("throw_force", TextInputField.field(PersistTextParser.FLOAT, Grapple::getThrowForce))
        .field("pull_force", TextInputField.field(PersistTextParser.FLOAT, Grapple::getPullForce))
        .field("pull_decay_rate", TextInputField.field(PersistTextParser.FLOAT, Grapple::getPullForceDecayRate))
        .field("min_pull_multiplier", TextInputField.field(PersistTextParser.FLOAT, Grapple::getMinPullForceMultiplier))
        .field("decay_duration", TextInputField.field(PersistTextParser.INTEGER, Grapple::getDecayDuration))

        .field("type_key", TextInputField.field(PersistTextParser.KEY, e -> ((SimpleDisplayGrapple) e).getTypeKey()))
        .field("display_type", TextInputField.field(PersistTextParser.STRING, e -> ((SimpleDisplayGrapple) e).getDisplayType()))
        .field("range", TextInputField.field(PersistTextParser.INTEGER, e -> ((SimpleDisplayGrapple) e).getMaxDistance()))
        .field("retract_speed", TextInputField.field(PersistTextParser.FLOAT, e -> ((SimpleDisplayGrapple) e).getRetractSpeed()))
        .apply(ctx ->{
            Key grappleType = ctx.get("grapple_type");
            int maxHooks = ctx.getOptional("max_hooks", 1);
            int cooldown = ctx.getOptional("cooldown", 2);
            float pullForce = ctx.getOptional("pull_force", .4f);
            float throwForce = ctx.getOptional("throw_force", .8f);

            return new SimpleDisplayGrapple(
                grappleType, maxHooks, cooldown, pullForce, throwForce,

                ctx.getOptional("pull_decay_rate", 1f),
                ctx.getOptional("min_pull_multiplier", .2f),
                ctx.getOptional("decay_duration", 20),

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

    public static final PersistTextParser<TeleportCheckInStructure> TELEPORT_STRUCTURE_CHECK = PersistTextParser.mapBuilder(TeleportCheckInStructure.class)
        .field("blacklist", TextInputField.field(ComponentInputParsers.KEY_PREDICATE, TeleportCheckInStructure::getBlacklist))
        .apply(ctx -> new TeleportCheckInStructure(
            ctx.getOptional("blacklist")
        ));

    public static final PersistTextParser<TeleportSetLastTP> TELEPORT_SET_LAST_TP = PersistTextParser.mapBuilder(TeleportSetLastTP.class)
        .apply(ctx -> new TeleportSetLastTP());
}
