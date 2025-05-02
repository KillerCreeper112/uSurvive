package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VaneGoal extends CruxMobModeledGoal {
    public static final Key STRONG_ATTACK_KEY = Crux.key("strong_attack");
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("vane"));

    public VaneGoal(@NotNull Mob mob) {
        this(KEY, mob);
    }

    public VaneGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
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
    protected int nextParticles = 0;
    @Override
    public void tick() {
        super.tick();

        tick++;
        if(tick > nextParticles){
            tick = 0;
            nextParticles = CruxMath.random(60, 80) / 2;
            spawnParticles();
        }
    }

    public void spawnParticles(){
        new ParticleBuilder(Particle.REVERSE_PORTAL)
            .location(mob.getLocation().add(0, mob.getHeight()/2, 0))
            .offset(1, 1, 1)
            .count(CruxMath.random(15, 20))
            .extra(0.2)
            .spawn();
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
