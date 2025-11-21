package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BounderGoal extends CruxMobGoal {
    public BounderGoal(@NotNull Mob mob) {
        this(defaultKey(), mob);
    }

    public BounderGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);

        sounds(
            new CruxGoalSounds(mob) {
                @Override
                public @Nullable CreateSound attack() {
                    return CreateSound.sound(Sound.ENTITY_PLAYER_ATTACK_WEAK,
                        net.kyori.adventure.sound.Sound.Source.HOSTILE,
                        0.3f, 1f);
                }

                @Override
                public @Nullable CreateSound hurt() {
                    return CreateSound.sound(Sound.ENTITY_PLAYER_HURT,
                        net.kyori.adventure.sound.Sound.Source.HOSTILE,
                        0.3f, 1f);
                }

                @Override
                public @Nullable CreateSound death() {
                    return CreateSound.sound(Sound.ENTITY_PLAYER_DEATH,
                        net.kyori.adventure.sound.Sound.Source.HOSTILE,
                        0.3f, 1f);
                }
            }
        );
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        if(!(target instanceof Player)) return false;
        return super.isValidNaturalTarget(target);
    }
}
