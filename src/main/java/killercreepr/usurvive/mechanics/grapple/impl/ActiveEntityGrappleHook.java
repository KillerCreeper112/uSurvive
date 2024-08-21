package killercreepr.usurvive.mechanics.grapple.impl;

import killercreepr.crux.data.Holder;
import killercreepr.crux.util.CruxEntity;
import killercreepr.usurvive.lang.Lang;
import killercreepr.usurvive.mechanics.grapple.ActiveGrappleHook;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ActiveEntityGrappleHook implements ActiveGrappleHook, Holder<Entity> {
    protected final @NotNull UUID uuid;
    protected final @NotNull Holder<Entity> entity;
    protected final int id;
    protected final double pullForce; //.04
    public ActiveEntityGrappleHook(@NotNull UUID uuid, @NotNull Holder<Entity> entity, int id, double pullForce) {
        this.uuid = uuid;
        this.entity = entity;
        this.id = id;
        this.pullForce = pullForce;
    }

    public ActiveEntityGrappleHook(@NotNull Entity entity, int id, double pullForce) {
        this.uuid = entity.getUniqueId();
        this.entity = Holder.weakReference(entity);
        this.id = id;
        this.pullForce = pullForce;
    }

    @Override
    public int getId() {
        return id;
    }
    @Override
    public boolean isValid(@NotNull Entity owner){
        Entity entity = value();
        return entity != null && entity.isValid() && owner.getWorld().equals(entity.getWorld());
    }

    @Override
    public void remove(){
        Entity entity = value();
        if(entity==null) return;
        entity.remove();
    }

    protected boolean wasCollidingLastTick = false;
    protected int tick = 0;
    @Override
    public void tick(@NotNull Entity owner) {
        Entity entity = value();
        if(!(entity instanceof LivingEntity mob)) return;
        tick++;

        if(tick < 5){
            wasCollidingLastTick = false;
        }else{
            wasCollidingLastTick = checkCollisionTick(owner, mob);
            if(wasCollidingLastTick) return;
        }

        //todo particle
        mob.setAI(true);
    }

    public boolean checkCollisionTick(@NotNull Entity owner, @NotNull LivingEntity entity){
        if(entity.wouldCollideUsing(entity.getBoundingBox().expand(.001))){
            if(!wasCollidingLastTick){
                Lang.GRAPPLE_HOOK.playAt(entity);
            }

            Vector vel = entity.getLocation().toVector().subtract(CruxEntity.getEyeOrLocation(owner).toVector())
                .multiply(pullForce);
            owner.setVelocity(owner.getVelocity().add(vel));
            entity.setAI(false);
            return true;
        }
        return false;
    }

    @Override
    public Entity value() {
        return entity.value();
    }
}
