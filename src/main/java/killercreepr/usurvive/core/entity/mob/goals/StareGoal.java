package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StareGoal extends CruxMobModeledGoal {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("stare"));

    public StareGoal(@NotNull Mob mob) {
        this(KEY, mob);
    }

    public StareGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @Nullable CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_CREAKING_TWITCH, 2f);
            }

            @Override
            public @Nullable CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_ENDERMAN_HURT, .3f, 0.5f);
            }

            @Override
            public @Nullable CreateSound step() {
                return CreateSound.sound(Sound.ENTITY_CREAKING_STEP, 2f);
            }

            @Override
            public @Nullable CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_CREAKING_ATTACK, 2f);
            }

            @Override
            public @Nullable CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_ENDERMAN_AMBIENT, 0.5f);
            }
        });
    }

    @Override
    public boolean shouldConstantlyLookAtTarget() {
        return true;
    }

    protected int tick = 0;
    protected int nextBlink = 0;
    @Override
    public void tick() {
        tick++;
        if(tick > nextBlink){
            tick = 0;
            nextBlink = CruxMath.random(60, 120) / 2;
            playAnimation("blink", true);
        }
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return target instanceof Player && super.isValidNaturalTarget(target);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(event.getRightClicked().equals(mob) || isModelPart(event.getRightClicked())){
            event.setCancelled(true);
        }
    }
}
