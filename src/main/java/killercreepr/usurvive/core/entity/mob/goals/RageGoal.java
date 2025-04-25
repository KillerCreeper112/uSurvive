package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class RageGoal extends CruxMobGoal {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("rage"));
    public RageGoal(@NotNull Mob mob) {
        super(KEY, mob);
    }

    @Override
    public void start() {
        super.start();
        if(CruxAttribute.hasAttributeData(mob)) return;
        //if the mob can already attack normally, don't use the custom system.
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(
            mob.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue() * 2D//todo fix this
        );
        if(mob.getAttribute(Attribute.ATTACK_DAMAGE) != null) return;
        double width = mob.getWidth();
        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(
            CruxMath.random(.5D, 1D) + width
        ));

        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(
            CruxMath.random(0D, .25D)
        ));

        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(
            CruxMath.random(-12D, -6D)
        ));

        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(
            CruxMath.random(5D, 10D)
        ));

        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(
            CruxMath.random(1D, 5D)
        ));
    }
}
