package killercreepr.usurvive.core.mechanics.grapple.impl;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.usurvive.core.lang.Lang;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ActiveControlledGrappleHook extends BasicGrappleHook implements Holder<Entity> {
    protected final @NotNull UUID uuid;
    protected final @NotNull Holder<Entity> entity;
    protected final int id;
    protected final double pullForce; //.04
    public ActiveControlledGrappleHook(@NotNull UUID uuid, @NotNull Holder<Entity> entity, int id, double pullForce) {
        this.uuid = uuid;
        this.entity = entity;
        this.id = id;
        this.pullForce = pullForce;
    }

    public ActiveControlledGrappleHook(@NotNull Entity entity, int id, double pullForce) {
        this.uuid = entity.getUniqueId();
        this.entity = Holder.weakReference(entity);
        this.id = id;
        this.pullForce = pullForce;
    }

    protected Double lastGravity;

    @Override
    public int getId() {
        return id;
    }
    @Override
    public boolean isValid(@NotNull Entity owner){
        Entity entity = value();
        return super.isValid(owner) && entity != null && entity.isValid() && owner.getWorld().equals(entity.getWorld());
    }

    @Override
    public void remove(Entity owner){
        super.remove(owner);
        Entity entity = value();
        if(owner != null) resetOwnerGravity(owner);
        if(entity==null) return;
        Crux.getServer().getScheduler().runTask(Crux.getMainPlugin(), task -> entity.remove());
    }

    protected boolean wasCollidingLastTick = false;
    @Override
    public void tick(@NotNull Entity owner) {
        Entity entity = value();
        if(!(entity instanceof LivingEntity mob)) return;

        Crux.getServer().getScheduler().runTask(Crux.getMainPlugin(), task ->{
            onTick(owner, mob);
        });
    }

    public void onTick(@NotNull Entity owner, @NotNull LivingEntity mob){
        if(isScheduledForRemoval()) return;
        wasCollidingLastTick = checkCollisionTick(owner, mob);
        if(wasCollidingLastTick) return;

        mob.setAI(true);
    }

    public void resetOwnerGravity(@NotNull Entity owner){
        if(lastGravity == null) return;
        if(!(owner instanceof LivingEntity l)) return;
        AttributeInstance instance = l.getAttribute(Attribute.GRAVITY);
        if(instance == null) return;
        instance.setBaseValue(lastGravity);
        lastGravity = null;
    }

    public void setOwnerGravity(@NotNull Entity owner, double gravity){
        if(lastGravity != null) return;
        if(!(owner instanceof LivingEntity l)) return;
        AttributeInstance instance = l.getAttribute(Attribute.GRAVITY);
        if(instance == null) return;
        lastGravity = instance.getBaseValue();
        instance.setBaseValue(gravity);
    }

    public boolean checkCollisionTick(@NotNull Entity owner, @NotNull LivingEntity entity){
        if(entity.wouldCollideUsing(entity.getBoundingBox().expand(.005))){
            if(!wasCollidingLastTick){
                Lang.GRAPPLE_HOOK.playAt(entity);
            }

            if(owner instanceof HumanEntity e && e.isSneaking()){
                Vector vel = entity.getLocation().toVector().subtract(CruxEntityUtil.getEyeOrLocation(owner).toVector())
                    .multiply(pullForce);
                owner.setVelocity(vel);
            }

            entity.setAI(false);
            setOwnerGravity(owner, 0D);
            return true;
        }
        for(Entity e : entity.getWorld().getNearbyEntities(entity.getBoundingBox().expand(.001),
            e -> !e.equals(entity) && !e.equals(owner))){
            EntityDamager.entityDamager(e, entity).attack(4D, 8D);
            Vector dir = entity.getEyeLocation().getDirection();
            Location hitLoc = e.getLocation();
            hitLoc.setY(entity.getY());
            Vector hit = hitLoc.toVector().normalize();
            Vector vel = CruxMath.reflect(dir, hit);
            vel.multiply(.75D);
            entity.setVelocity(entity.getVelocity().add(vel));
            return true;
        }
        resetOwnerGravity(owner);
        return false;
    }

    @Override
    public Entity value() {
        return entity.value();
    }
}
