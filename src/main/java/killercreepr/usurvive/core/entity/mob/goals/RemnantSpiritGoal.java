package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.persistence.CruxPersist;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class RemnantSpiritGoal extends CruxMobModeledGoal implements Listener {
    public RemnantSpiritGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {

            @Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_PLAYER_ATTACK_WEAK, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,1f);
            }
            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_PLAYER_HURT, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,1f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_PLAYER_DEATH, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,1f);
            }
        });
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        if(target.getUniqueId().equals(CruxPersist.OWNER.get(mob, null))) return false;
        return super.isValidNaturalTarget(target);
    }

    @Override
    public void tick() {
        super.tick();
        if(Crux.getCurrentTick() % 40 == 0){
            particleTick();
        }
    }

    public void particleTick(){
        new ParticleBuilder(Particle.SOUL)
            .location(mob.getLocation().add(0, mob.getHeight()*.5, 0))
            .offset(.3, .3, .3)
            .count(CruxMath.random(5, 8))
            .extra(.05)
            .spawn();
        new ParticleBuilder(Particle.SOUL_FIRE_FLAME)
            .location(mob.getLocation().add(0, mob.getHeight()*.5, 0))
            .offset(.3, .3, .3)
            .count(CruxMath.random(5, 8))
            .extra(.05)
            .spawn();
    }
}
