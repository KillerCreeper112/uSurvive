package killercreepr.usurvive.core.component;

import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.api.component.parser.hybrid.TextInputField;
import killercreepr.crux.core.component.parser.type.ComponentInputParsers;
import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import killercreepr.cruxattributes.core.component.CruxAttributeCompParsers;
import killercreepr.usurvive.api.component.InfusedPotionGroup;
import killercreepr.usurvive.api.component.InfusedPotions;
import killercreepr.usurvive.api.mechanics.Grapple;
import killercreepr.usurvive.core.component.impl.SimpleInfusedPotionGroup;
import killercreepr.usurvive.core.component.impl.SimpleInfusedPotions;
import killercreepr.usurvive.core.mechanics.grapple.SimpleDisplayGrapple;
import killercreepr.usurvive.core.mechanics.grapple.SimpleGrapple;
import net.kyori.adventure.key.Key;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
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
        .apply(ctx ->{
            Key grappleType = ctx.get("grapple_type");
            int maxHooks = ctx.getOptional("max_hooks", 1);
            int cooldown = ctx.getOptional("cooldown", 0);
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
            int cooldown = ctx.getOptional("cooldown", 0);
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

            /*return new SimpleDisplayGrapple(
                grappleType, maxHooks, cooldown, throwForce,
                ctx.get("type_key"),
                ctx.getOptional("display_type", "block"),
                ctx.getOptional("pull_force", 0.2f),
                ctx.getOptional("range", 16),
                ctx.getOptional("throw_speed", .8D),
                ctx.getOptional("retract_speed", .5f),

                ctx.getOptional("pull_decay_rate", 1f),
                ctx.getOptional("min_pull_multiplier", .2f),
                ctx.getOptional("decay_duration", 20)
            );*/
        });

    public static final PersistTextParser<InfusedPotionGroup> INFUSED_POTION_GROUP = PersistTextParser.mapBuilder(InfusedPotionGroup.class)
        .field("key", TextInputField.field(PersistTextParser.KEY, InfusedPotionGroup::key))
        .field("potions", TextInputField.field(ComponentInputParsers.LIST.POTION_EFFECT, InfusedPotionGroup::getPotionEffects))
        .field("slot", TextInputField.field(CruxAttributeCompParsers.CRUX_SLOT_GROUP, InfusedPotionGroup::getSlot))
        .apply(ctx ->{
            Key key = ctx.get("key");
            List<PotionEffect> potions = ctx.get("potions");
            CruxSlotGroup slot = ctx.getOptional("slot", CruxSlotGroup.ANY);
            return new SimpleInfusedPotionGroup(key, potions, slot);
        });

    public static final PersistTextParser<InfusedPotions> INFUSED_POTIONS = PersistTextParser.mapBuilder(InfusedPotions.class)
        .field("infusion", TextInputField.field(PersistTextParser.collectionList(INFUSED_POTION_GROUP), InfusedPotions::getInfusedPotions))
        .apply(ctx ->{
            Collection<InfusedPotionGroup> infusion = ctx.get("infusion");
            return new SimpleInfusedPotions(infusion);
        });
}
