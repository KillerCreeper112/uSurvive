package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WhispershadeGoal extends CruxMobModeledGoal implements Listener {
    public WhispershadeGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_VEX_AMBIENT, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,.7f);
            }

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_VEX_HURT, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,.7f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_VEX_DEATH, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,.7f);
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onTestDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        if(event instanceof EntityDamageByBlockEvent eDmg){
            if(eDmg.getDamager() == null) return;
            if(eDmg.getDamager().isLiquid()){
                event.setCancelled(true);
                return;
            }
        }
    }


    protected final Map<UUID, Long> attackers = new HashMap<>();
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        playAnimation("hurt", false);

        Location loc;
        if(event instanceof EntityDamageByEntityEvent eDmg){
            if(CruxEntityUtil.isNonSurvival(eDmg.getDamager())) return;
            loc = eDmg.getDamager().getLocation();
            Entity owner = EntityDamager.getOwner(eDmg.getDamager());
            if(owner != null){
                attackers.put(owner.getUniqueId(), System.currentTimeMillis());
            }
        }else if(event instanceof EntityDamageByBlockEvent eDmg){
            if(eDmg.getDamager() == null) return;
            loc = eDmg.getDamager().getLocation().toCenterLocation();
        }else return;
        Location tp = findSafeTeleport(loc);
        if(tp == null) return;

        CruxLoc.lookAt(tp, loc);


        new ParticleBuilder(Particle.SQUID_INK)
            .extra(.3)
            .offset(.6, .6, .6)
            .count(CruxMath.random(5, 7))
            .location(mob.getLocation().add(0, mob.getHeight()/2, 0))
            .spawn();
        mob.teleport(tp);
    }

    public boolean hasAttackedWithin(Entity e, int ticks){
        if(e==null) return false;
        Long time = attackers.get(e.getUniqueId());
        if(time == null) return false;
        return CruxMath.hasOccurredWithin(time, ticks);
    }

    public Location findSafeTeleport(Location center) {
        Vector direction = center.getDirection().normalize().multiply(-1); // opposite of where they're facing
        //World world = center.getWorld();

        int attempts = 10; // number of positions to try
        double minDistance = 2.0;
        double maxDistance = 6.0;
        double angleSpread = Math.toRadians(60); // 60° cone behind

        for (int i = 0; i < attempts; i++) {
            double angle = (Math.random() - 0.5) * angleSpread; // random angle left/right within cone
            double distance = minDistance + Math.random() * (maxDistance - minDistance);

            // Rotate direction around Y-axis by angle
            Vector rotated = direction.clone().rotateAroundY(angle).normalize().multiply(distance);

            Location target = center.clone().add(rotated);
            target.setY(center.getY()); // keep Y consistent for now

            // Search slightly up/down to find a safe spot
            for (int yOffset = -1; yOffset <= 2; yOffset++) {
                Location check = target.clone().add(0, yOffset, 0);
                if (isSafeTeleport(check.getBlock())) {
                    return check;
                }
            }
        }
        return null;
    }


    public boolean isSafeTeleport(Block b) {
        Block head = b.getRelative(BlockFace.UP);
        Block aboveHead = head.getRelative(BlockFace.UP);
        return isEmptyOrPassable(b)
            && isEmptyOrPassable(head)
            && isEmptyOrPassable(aboveHead);
    }

    public boolean isEmptyOrPassable(Block b){
        return b.isEmpty() || b.isPassable();
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return target instanceof Player && !CruxEntityUtil.isNonSurvival(target);
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

    public int getMaxSameTargetTicks(){
        return 200;
    }

    protected int sameTargetTicks = 0;
    @Override
    public void tick() {
        if(despawn){
            lookAtTarget();
            if(!isPlayingAnimation("despawn")){
                mob.remove();
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
            lookAtTarget();
            if(isPlayingAnimation("scream")) return;
            mob.setRemoveWhenFarAway(true);
            mob.setPersistent(false);
            playAnimation("despawn", true);
            despawn = true;

            CreateSound.sound(Sound.ENTITY_GHAST_SCREAM, CruxMath.random(0.7f, 1.4f)).playAt(mob);
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
            target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 1));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
            return;
        }

        LivingEntity lastTarget = target;
        super.checkTargetLogic();
        if(target==null) return;

        if(hasAttackedWithin(target, 7200)){
            super.targetLogic();
        }else{
            lookAtTarget();
        }

        if(target.equals(lastTarget)){
            sameTargetTicks++;
        }else sameTargetTicks = 0;

        if(!isWearingHeadGear(target) && !isMutualEyeContact(target, mob, 0.2D, 0.45D)){
            if(sameTargetTicks > getMaxSameTargetTicks() && !hasAttackedWithin(target, 600)){
                setTarget(null);
                sameTargetTicks = 0;
            }
            return;
        }
        scream = true;
        playAnimation("scream", true);
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if(!mob.equals(event.getHitEntity())) return;
        event.setCancelled(true);
    }

}
