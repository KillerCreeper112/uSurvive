package killercreepr.usurvive.core.component;

import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.api.component.parser.hybrid.TextInputField;
import killercreepr.usurvive.api.mechanics.Grapple;
import killercreepr.usurvive.core.mechanics.grapple.SimpleDisplayGrapple;
import killercreepr.usurvive.core.mechanics.grapple.SimpleGrapple;
import net.kyori.adventure.key.Key;

public class USurviveCompParsers {
    public static final PersistTextParser<Grapple> GRAPPLE = PersistTextParser.mapBuilder(Grapple.class)
        .field("grapple_type", TextInputField.field(PersistTextParser.KEY, Grapple::getHookType))
        .field("max_hooks", TextInputField.field(PersistTextParser.INTEGER, Grapple::getMaxHooks))
        .field("throw_force", TextInputField.field(PersistTextParser.FLOAT, Grapple::getThrowForce))
        .apply(ctx ->{
            Key grappleType = ctx.get("grapple_type");
            int maxHooks = ctx.getOptional("max_hooks", 1);
            int cooldown = ctx.getOptional("cooldown", 0);
            float throwForce = ctx.getOptional("throw_force", 1f);
            return new SimpleGrapple(grappleType, maxHooks, cooldown, throwForce);
        });

    public static final PersistTextParser<Grapple> GRAPPLE_DISPLAY = PersistTextParser.mapBuilder(Grapple.class)
        .field("grapple_type", TextInputField.field(PersistTextParser.KEY, Grapple::getHookType))
        .field("max_hooks", TextInputField.field(PersistTextParser.INTEGER, Grapple::getMaxHooks))
        .field("throw_force", TextInputField.field(PersistTextParser.FLOAT, Grapple::getThrowForce))
        .field("type_key", TextInputField.field(PersistTextParser.KEY, e -> ((SimpleDisplayGrapple) e).getTypeKey()))
        .field("display_type", TextInputField.field(PersistTextParser.STRING, e -> ((SimpleDisplayGrapple) e).getDisplayType()))
        .field("pull_force", TextInputField.field(PersistTextParser.DOUBLE, e -> ((SimpleDisplayGrapple) e).getPullForce()))
        .field("range", TextInputField.field(PersistTextParser.INTEGER, e -> ((SimpleDisplayGrapple) e).getMaxDistance()))
        .field("throw_speed", TextInputField.field(PersistTextParser.DOUBLE, e -> ((SimpleDisplayGrapple) e).getThrowSpeed()))
        .field("retract_speed", TextInputField.field(PersistTextParser.DOUBLE, e -> ((SimpleDisplayGrapple) e).getRetractSpeed()))
        .field("decay_rate", TextInputField.field(PersistTextParser.DOUBLE, e -> ((SimpleDisplayGrapple) e).getPullForceDecayRate()))
        .field("min_pull_force_multiplier", TextInputField.field(PersistTextParser.DOUBLE, e -> ((SimpleDisplayGrapple) e).getMinPullForceMultiplier()))
        .field("decay_duration", TextInputField.field(PersistTextParser.INTEGER, e -> ((SimpleDisplayGrapple) e).getDecayDuration()))
        .apply(ctx ->{
            Key grappleType = ctx.get("grapple_type");
            int maxHooks = ctx.getOptional("max_hooks", 1);
            int cooldown = ctx.getOptional("cooldown", 0);
            float throwForce = ctx.getOptional("throw_force", 1f);
            return new SimpleDisplayGrapple(
                grappleType, maxHooks, cooldown, throwForce,
                ctx.get("type_key"),
                ctx.getOptional("display_type", "block"),
                ctx.getOptional("pull_force", 0.2D),
                ctx.getOptional("range", 16),
                ctx.getOptional("throw_speed", .8D),
                ctx.getOptional("retract_speed", .5D),

                ctx.getOptional("decay_rate", 1D),
                ctx.getOptional("min_pull_force_multiplier", .2D),
                ctx.getOptional("decay_duration", 20)
            );
        });
}
