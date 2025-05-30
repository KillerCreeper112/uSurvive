package killercreepr.usurvive.core.mechanics.grapple.impl;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.usurvive.api.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.api.mechanics.grapple.context.GrappleContext;
import killercreepr.usurvive.core.lang.Lang;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public class ActiveEntityGrappleHook extends BasicGrappleHook implements Holder<Entity> {
    protected final @NotNull UUID uuid;
    protected final @NotNull Holder<Entity> entity;
    protected final int id;
    public ActiveEntityGrappleHook(@NotNull UUID uuid, @NotNull Holder<Entity> entity, GrappleContext ctx) {
        super(ctx);
        this.uuid = uuid;
        this.entity = entity;
        this.id = ctx.getId();
    }

    public ActiveEntityGrappleHook(@NotNull Entity entity, GrappleContext ctx) {
        super(ctx);
        this.uuid = entity.getUniqueId();
        this.entity = Holder.weakReference(entity);
        this.id = ctx.getId();
    }

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
        if(entity==null) return;
        Crux.getServer().getScheduler().runTask(Crux.getMainPlugin(), task -> entity.remove());
    }

    @Override
    public Location getHookLocation() {
        return entity.value().getLocation();
    }

    protected boolean wasCollidingLastTick = false;
    @Override
    public void tick(@NotNull Entity owner, @NotNull Collection<ActiveGrappleHook> activeHooks) {
        Entity entity = value();
        if(!(entity instanceof LivingEntity mob)) return;

        Crux.scheduler().runTask(() -> onTick(owner, mob));
    }

    public void onTick(@NotNull Entity owner, @NotNull LivingEntity mob){
        if(isScheduledForRemoval()) return;
        wasCollidingLastTick = checkCollisionTick(owner, mob);
        if(wasCollidingLastTick) return;

        mob.setAI(true);
    }

    public void pullTowardsTarget(Entity owner, Location target){
        // Calculate how long the grapple has been attached
        long timeAttached = (System.currentTimeMillis() - grappleAttachTime)/50L;

        // Calculate the decay rate based on time spent on the block
        double pullForceMultiplier = Math.max(1.0 - (timeAttached / (float) decayDuration) * pullForceDecayRate, minPullForceMultiplier);

        // Adjust velocity based on the calculated pull force
        Vector vel = target.toVector().subtract(CruxEntityUtil.getEyeOrLocation(owner).toVector())
            .multiply(pullForce * pullForceMultiplier); // Apply the multiplier to the pull force
        double velDistance = vel.length();
        double scaledForce = Math.min(velDistance * 0.1, pullForce * 2);

        vel.normalize().multiply(scaledForce);
        owner.setVelocity(owner.getVelocity().add(vel));
    }

    public boolean checkCollisionTick(@NotNull Entity owner, @NotNull LivingEntity entity){
        if(entity.wouldCollideUsing(entity.getBoundingBox().expand(.001))){
            if(!wasCollidingLastTick){
                Lang.GRAPPLE_HOOK.playAt(entity);
                onGrappleAttach();
            }

            pullTowardsTarget(owner, entity.getLocation());
            /*Vector vel = entity.getLocation().toVector().subtract(CruxEntityUtil.getEyeOrLocation(owner).toVector())
                .multiply(pullForce);
            owner.setVelocity(owner.getVelocity().add(vel));*/
            entity.setAI(false);
            return true;
        }else grappleAttachTime = -1;
        for(Entity e : entity.getWorld().getNearbyEntities(entity.getBoundingBox().expand(.001),
            e -> !e.equals(entity) && !e.equals(owner))){
            onEntityHit(owner, e);
            /*EntityDamager.entityDamager(e, entity).attack(4D, 8D);
            Vector dir = entity.getEyeLocation().getDirection();
            Location hitLoc = e.getLocation();
            hitLoc.setY(entity.getY());
            Vector hit = hitLoc.toVector().normalize();
            Vector vel = CruxMath.reflect(dir, hit);
            vel.multiply(.75D);
            entity.setVelocity(entity.getVelocity().add(vel));*/
            return true;
        }
        return false;
    }

    @Override
    public Entity value() {
        return entity.value();
    }
}
