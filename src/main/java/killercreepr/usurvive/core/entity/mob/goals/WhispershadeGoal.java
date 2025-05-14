package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttributeInstance;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class WhispershadeGoal extends CruxMobModeledGoal implements Listener {
    public WhispershadeGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_VEX_AMBIENT, .7f);
            }

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_VEX_HURT, .7f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_VEX_DEATH, .7f);
            }
        });
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


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        playAnimation("hurt", false);
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return target instanceof Player;
    }

    @Override
    protected int getFindCooldownMax() {
        return 12;
    }

    protected boolean scream = false;
    public boolean isScreaming(){
        return scream;
    }

    protected boolean despawn = false;

    public boolean isWearingHeadGear(LivingEntity e){
        var equip = e.getEquipment();
        if(equip == null) return false;
        ItemStack item = equip.getHelmet();
        if(CruxItem.isEmpty(item)) return false;
        return item.getType() == Material.CARVED_PUMPKIN;
    }

    @Override
    public void tick() {
        if(despawn){
            if(!isPlayingAnimation("despawn")){
                mob.remove();
                CreateSound.sound(Sound.ENTITY_GHAST_SCREAM, CruxMath.random(0.7f, 1.4f)).playAt(mob);
                new ParticleBuilder(Particle.PORTAL)
                    .extra(.3)
                    .offset(.4, .4, .4)
                    .count(CruxMath.random(9, 13))
                    .location(mob.getLocation().add(0, mob.getHeight()/2, 0))
                    .spawn();
            }
            return;
        }
        if(isScreaming()){
            if(target == null || !target.isValid()){
                scream = false;
                stopAnimation("scream");
                return;
            }
            if(isPlayingAnimation("scream")) return;
            mob.setRemoveWhenFarAway(true);
            mob.setPersistent(false);
            playAnimation("despawn", true);
            despawn = true;

            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
            target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 1));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
            return;
        }

        super.checkTargetLogic();
        if(target==null) return;

        if(!isWearingHeadGear(target) && !isMutualEyeContact(target, mob, 0.2D, 0.4D)){
            setTarget(null);
            return;
        }
        scream = true;
        playAnimation("sceam", true);
        CreateSound.sound(Sound.ENTITY_GHAST_HURT, 1.6f).playAt(mob);
        new ParticleBuilder(Particle.SQUID_INK)
            .extra(.3)
            .offset(.6, .6, .6)
            .count(CruxMath.random(9, 13))
            .location(mob.getLocation().add(0, mob.getHeight()/2, 0))
            .spawn();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(event.getRightClicked().equals(mob) || isModelPart(event.getRightClicked())){
            event.setCancelled(true);
        }
    }
}
