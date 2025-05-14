package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.cruxpotions.api.potion.inflictor.PotionInflictor;
import killercreepr.cruxpotions.core.entity.memory.SimplePotionHolder;
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import killercreepr.usurvive.core.potion.USurvivePotions;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VaneGoal extends CruxMobModeledGoal {
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

    @Override
    protected void targetAttackLogic(double distance) {
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
            return;
        }
        if(target==null) return;

        if(!isMutualEyeContact(target, mob, 0.2D, 0.3D)) return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 400, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 1));

        var holder = EntityMemory.getDataHolder(target, SimplePotionHolder.class);
        if(holder != null){
            holder.addPotion(
                USurvivePotions.DISORIENTATION.create(target, 200, 1, new EntityInflictor(mob))
            );
        }
    }

    public boolean isMutualEyeContact(LivingEntity player, LivingEntity mob, double maxAngle, double mobMaxAngle) {
        if (!player.getWorld().equals(mob.getWorld())) return false;
        if (!player.hasLineOfSight(mob)) return false;

        Location playerEyes = player.getEyeLocation();
        Location mobEyes = mob.getEyeLocation();

        Vector toMob = mobEyes.toVector().subtract(playerEyes.toVector()).normalize();
        Vector toPlayer = playerEyes.toVector().subtract(mobEyes.toVector()).normalize();

        Vector playerLook = playerEyes.getDirection().normalize();
        Vector mobLook = mobEyes.getDirection().normalize();

        //double maxAngle = 0.1; // ~5.7 degrees

        // Player must be looking nearly directly at the mob's face
        boolean playerLookingAtMob = playerLook.angle(toMob) < maxAngle;

        // Mob must be looking nearly directly at the player
        boolean mobFacingPlayer = mobLook.angle(toPlayer) < mobMaxAngle;

        return playerLookingAtMob && mobFacingPlayer;
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
