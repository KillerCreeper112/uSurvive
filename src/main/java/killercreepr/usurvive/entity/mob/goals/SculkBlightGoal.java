package killercreepr.usurvive.entity.mob.goals;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.Crux;
import killercreepr.crux.data.communication.CreateSound;
import killercreepr.crux.event.CruxEntityDamageEvent;
import killercreepr.crux.util.CruxLoc;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SculkBlightGoal extends CruxMobModeledGoal {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("sculk_blight"));

    public SculkBlightGoal(@NotNull Mob mob, ActiveModel model) {
        super(KEY, mob, model);
    }

    public SculkBlightGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob, ActiveModel model) {
        super(key, mob, model);
    }


    @EventHandler(ignoreCancelled = true)
    public void entityDamage(EntityDamageEvent event){
        if(mob.equals(event.getEntity())){
            switch (event.getCause()){
                case SUFFOCATION, FALLING_BLOCK, FALL -> event.setCancelled(true);
            }
        }
    }

    protected Entity drag;
    protected final double dragDownSpeed = .2;
    @Override
    protected void attacked(@NotNull CruxEntityDamageEvent event) {
        super.attacked(event);
        if(event.getEntity().getVehicle() != null) return;
        drag = event.getEntity();
        mob.addPassenger(drag);
        playAnimation("attacking", .25D, .25D, 1D, true);
    }

    public boolean isUnderground(@NotNull Entity mob){
        Block b;
        if(mob instanceof LivingEntity living){
            b = living.getEyeLocation().getBlock();
            if(!b.isSolid()) return false;
        }
        b = mob.getLocation().getBlock();
        if(!b.isSolid()) return false;
        return true;
    }

    protected void onEntityDragged(@NotNull Entity e){
        stopAnimation("idle", true);
        //e.leaveVehicle();
        attemptAttack((LivingEntity) e, 0D);
        //new CruxEntityDamager(e, mob).attack(.1D, 0D);
        /*if(e instanceof LivingEntity le){
            new CruxEntityDamager(e, mob).attack(.1D, 0D);
            double damage = CruxMath.random(1D, 30D);
            le.damage(damage, mob);
            if(!e.isValid()) return;
        }*/
        //todo new SculkedMob(e).infect();
    }

    @Override
    public void tick() {
        if(drag != null){
            if(!drag.isValid()){
                drag = null;
                stopAnimation("attacking", true);
                playAnimation("idle", .2D, .2D, 1D, false);

                new ParticleBuilder(Particle.SCULK_SOUL)
                    .count(CruxMath.random(1, 3))
                    .location(mob.getEyeLocation().add(0, 1, 0))
                    .offset(0,0,0)
                    .extra(.1)
                    .spawn()
                ;
                return;
            }
            onEntityDragged(drag);
            /*Location tp = mob.getLocation().subtract(0, dragDownSpeed, 0);

            if(isUnderground(mob)){
                new ParticleBuilder(Particle.SCULK_SOUL)
                    .count(CruxMath.random(1, 3))
                    .location(mob.getEyeLocation().add(0, 1, 0))
                    .offset(0,0,0)
                    .extra(.1)
                    .spawn()
                    ;

                tp.subtract(0, dragDownSpeed, 0);
                onEntityDragged(drag);
                drag = null;
            }

            mob.teleport(tp, TeleportFlag.EntityState.RETAIN_PASSENGERS);
            diggingUndergroundTick();*/
            return;
        }
        stopAnimation("digging");

        if(target == null){
            if(noTargetTick()) return;
        }
        if(goUnderGround){
            if(goUndergroundTick()) return;
        }
        super.tick();
    }

    protected final int goUndergroundAfterNoTarget = 100;
    private int noTargetTicks = 0;
    private boolean goUnderGround = false;
    private Location targetLoc;

    private final double moveDistance = .35D;
    private final CreateSound digSound = new CreateSound(Sound.BLOCK_SCULK_BREAK, 1.6f);

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target){
        if(CruxMob.isInCategory(target, MobCategory.MONSTER, MobCategory.SCULK)) return false;
        if(goUnderGround) return isValidTarget(target);
        return isValidTarget(target) && hasLineOfSight(target);
    }

    protected int digAnimation = 0;
    public boolean noTargetTick(){
        if(goUnderGround){
            if(targetLoc != null) targetLoc = null;
            Block current = mob.getLocation().getBlock();
            if(current.isSolid() && current.getRelative(BlockFace.UP).isSolid() && current.getRelative(BlockFace.UP).getRelative(BlockFace.UP).isSolid()){
                if(findTargetCooldown > 0){
                    findTargetCooldown--;
                    return true;
                }
                if(!findAndSetTarget(targetCheck)){
                    findTargetCooldown = 40;
                    target = null;
                    mob.setTarget(null);
                }
                return true;
            }
            Block b = mob.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if(b.isSolid() && b.getRelative(BlockFace.DOWN).isSolid() &&
                b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).isSolid()){
                if(isPlayingAnimation("dig")){
                    stopAnimation("idle", true);
                    digAnimation++;
                    //34
                    if(digAnimation >= 17){
                        digSound.playAt(mob.getLocation());

                        new ParticleBuilder(Particle.BLOCK)
                            .count(10)
                            .location(mob.getLocation())
                            .offset(.5, .5, .5)
                            .extra(.1)
                            .data(b.getBlockData())
                            .spawn()
                        ;
                    }
                    if(digAnimation < 21) return true;
                }

                if(digAnimation == 0){
                    digAnimation = 1;
                    playAnimation("dig", true);
                    return true;
                }

                Location l = mob.getLocation();
                l.setPitch(90f);
                l.subtract(0, moveDistance, 0);
                mob.teleport(l);
                digSound.playAt(mob.getLocation());

                new ParticleBuilder(Particle.BLOCK)
                    .count(10)
                    .location(mob.getLocation())
                    .offset(.5, .5, .5)
                    .extra(.1)
                    .data(b.getBlockData())
                    .spawn()
                ;
            }
            return true;
        }
        digAnimation = 0;
        if(noTargetTicks++ > goUndergroundAfterNoTarget){
            noTargetTicks = 0;
            setGoUnderGround(true);
            return true;
        }
        return false;
    }

    public void setGoUnderGround(boolean goUnderGround) {
        this.goUnderGround = goUnderGround;
    }

    protected int circleAround = 0;
    protected final int circleAmountMax = 100;
    protected int circleAroundIndex = 0;
    public boolean goUndergroundTick(){
        if(targetLoc == null){
            targetLoc = findCircleLocation();
            circleAroundIndex = 0;
            circleAround = 0;
        }
        Block current = mob.getLocation().getBlock();
        if(current.isSolid()){
            digSound.playAt(mob.getLocation());
            new ParticleBuilder(Particle.BLOCK)
                .count(10)
                .location(mob.getLocation())
                .offset(.5, .5, .5)
                .extra(.1)
                .data(current.getBlockData())
                .spawn()
            ;

            Block surface = mob.getLocation().add(0, .3, 0).getBlock();
            if(surface.isEmpty() || surface.isPassable()){
                setGoUnderGround(false);
                playAnimation("dig_jump", .2D, .2D, 1D, false);
                //mob.lookAt(target);
                Vector vel = target.getEyeLocation().toVector().subtract(mob.getLocation().toVector()).normalize();
                vel.setY(CruxMath.random(.5D, .6D));
                mob.setVelocity(vel);
                targetLoc = null;
                mob.lookAt(target);
                mob.getWorld().playSound(mob.getLocation(), Sound.ENTITY_HOGLIN_ANGRY, 1f, 1.6f);
                return false;
            }
            Location l = CruxLoc.lookAt(mob.getLocation(), target.getEyeLocation());
            mob.lookAt(targetLoc);
            mob.teleport(CruxLoc.shiftToward(l, targetLoc, moveDistance));

            if(circleAround < circleAmountMax){
                circleAround++;
                if(mob.getLocation().distanceSquared(targetLoc) <= (1.5D*1.5D)){
                    targetLoc = findCircleLocation();
                }
                Block particleBlock = findSolidBlockAboveWithAir(mob.getLocation().getBlock(), 3);
                if(particleBlock != null){
                    Location parLoc = particleBlock.getLocation().toCenterLocation().add(0, 1, 0);
                    ParticleBuilder par = new ParticleBuilder(Particle.BLOCK)
                        .count(10)
                        .location(parLoc)
                        .offset(.2, .2, .2)
                        .extra(.1)
                        .data(current.getBlockData())
                        .spawn()
                    ;

                    if(circleAround % 10 == 0){
                        par.particle(Particle.SCULK_CHARGE)
                            .data(0f)
                            .count(2)
                            .offset(.01, .01, .01)
                            .spawn();
                    }
                    if(circleAround >= circleAmountMax){
                        for(Block b : CruxLoc.getNearbyBlocks(target.getLocation().getBlock(), 5)){
                            if((b.getRelative(BlockFace.UP).isPassable() || b.getRelative(BlockFace.UP).isEmpty()) &&
                                target.getLocation().distanceSquared(b.getLocation().toCenterLocation()) > (3*3)){
                                targetLoc = b.getLocation().toCenterLocation().add(0, .5, 0);
                                break;
                            }
                        }
                    }
                }
            }
            return true;
        }
        setGoUnderGround(false);
        targetLoc = null;
        return false;
    }

    public @NotNull Location findCircleLocation(){
        List<Location> list = CruxLoc.getCircle(target.getLocation().subtract(0, 1, 0), 5D);
        Collections.shuffle(list);
        for(Location l : list){
            Block b = findSolidLocation(l.getBlock(), 3);
            if(b != null) return b.getLocation().toCenterLocation();
        }
        return CruxMath.getRandom(list);
    }

    public @Nullable Block findSolidBlockAboveWithAir(@NotNull Block b, int range){
        for(int y = 0; y <= range; y++){
            Block check = b.getRelative(0, y, 0);
            if(!check.isSolid()) continue;
            Block a = check.getRelative(BlockFace.UP);
            if(a.isEmpty() || a.isPassable()) return check;
        }
        return null;
    }

    public @Nullable Block findSolidLocation(@NotNull Block b, int range){
        for(int y = 0; y >= -range; y--){
            Block check = b.getRelative(0, y, 0);
            if(check.isSolid()) return check;
        }
        return null;
    }
}
