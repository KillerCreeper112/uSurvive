package killercreepr.usurvive.core.entity.mob;

import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.usurvive.core.entity.mob.goals.AggressiveZombieHorseGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AggressiveZombieHorse extends SimpleCruxMob {
    public AggressiveZombieHorse(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, ZombieHorse.class, e ->{
            load(e);

            double width = e.getWidth();
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(width + 1.1D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(5.2D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(14D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK_UP, CruxAttributeModifier.baseModifier(4D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-11));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(0.2));
            CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.baseModifier(e.getAttribute(Attribute.MOVEMENT_SPEED).getValue()));
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CruxGoalUtil.removeGoals(mob, List.of(
            "minecraft:panic", "minecraft:horse_run_around_like_crazy", "minecraft:follow_parent", "minecraft:breed"
        ));

        CruxGoalUtil.addIfNotPresent(mob, AggressiveZombieHorseGoal.class, 0, () -> new AggressiveZombieHorseGoal(mob));
    }

    @Override
    public Set<MobCategory> getCategories() {
        return Set.of(
            MobCategory.ENEMY, MobCategory.MONSTER, MobCategory.UNDEAD
        );
    }
}
