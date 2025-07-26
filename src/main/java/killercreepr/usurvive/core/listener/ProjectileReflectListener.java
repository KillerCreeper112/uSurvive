package killercreepr.usurvive.core.listener;

import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.persistence.CruxPersist;
import killercreepr.crux.core.persistence.PersistTag;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeInstance;
import killercreepr.cruxattributes.core.component.CruxAttributeComponents;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.damage.type.CruxEntityDamageTypes;
import killercreepr.usurvive.api.event.ProjectileRicochetEvent;
import killercreepr.usurvive.core.entity.memory.PiercingProjectileHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class ProjectileReflectListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();

        if(event.getHitEntity() != null){
            projectileHitEntity(proj, event);
            return;
        }
        if(event.getHitBlock() != null){
            projectileHitBlock(proj, event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProjectileHitLowest(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();
        Entity hit = event.getHitEntity();
        if(hit == null) return;
        PiercingProjectileHolder holder = EntityMemory.getDataHolder(proj, PiercingProjectileHolder.class);
        if(holder == null) return;

        if(CruxMath.hasOccurredWithin(holder.getLastHit().getOrDefault(hit.getUniqueId(), 0L), 10L)){
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileHitHighest(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();
        Entity hit = event.getHitEntity();
        if(hit == null) return;

        if(!CruxAttribute.hasAttributeData(proj)) return;
        var builder = DamageSource.builder(CruxEntityDamageTypes.ENTITY_PROJECTILE_CALCULATE_CUSTOM)
            .withDirectEntity(proj);

        var shooter = EntityDamager.getOwner(proj);
        if(shooter != null && !proj.equals(shooter)){
            builder.withCausingEntity(shooter);
        }

        int pierce = (int) CruxAttribute.get(proj, CruxAttribute.ATTACK_PIERCE);

        Location loc = proj.getLocation();
        EntityDamager dmger = EntityDamager.entityDamager(hit, proj)
            .setHitPosition(loc)
            .setSource(builder.withDamageLocation(loc).build());

        if(pierce < 1){
            if(dmger.isDamageSourceBlocked()){
                return;
            }
            dmger.attack();
            return;
        }
        int currentPierce = CruxPersist.PIERCED_ENTITIES_AMOUNT.get(proj, 0);
        if(currentPierce >= pierce) return;
        PiercingProjectileHolder holder = EntityMemory.getOrCreateDataHolder(proj, PiercingProjectileHolder.class, PiercingProjectileHolder::new);
        if(holder != null){
            holder.getLastHit().put(hit.getUniqueId(), System.currentTimeMillis());
        }
        CruxPersist.PIERCED_ENTITIES_AMOUNT.set(proj, currentPierce+1);
        event.setCancelled(true);
        dmger.attack();
    }


    public void projectileHitEntity(Projectile proj, ProjectileHitEvent event){
        CruxAttributeInstance instance = CruxAttribute.getInstance(proj, CruxAttribute.PROJECTILE_RICOCHET_ENTITY);
        if(instance == null) return;
        int reflected = CruxPersist.RICOCHET_BLOCK.get(proj, 0);
        int max = (int) instance.getValue();
        if(reflected >= (max-1)) return;

        Vector normal = proj.getLocation().toVector().normalize();

        ProjectileRicochetEvent x = reflectProjectile(proj, normal, reflected,
            CruxAttribute.get(proj, CruxAttribute.PROJECTILE_RICOCHET_ENTITY_VELOCITY_DROP_OFF, .7D),
            CruxPersist.RICOCHET_ENTITY, event.getHitBlock(), event.getHitBlockFace(), event.getHitEntity(),
            null
        );
        if(x.isCancelled()){
            x.getNewProjectile().remove();
            return;
        }
        proj.remove();
    }

    public void projectileHitBlock(Projectile proj, ProjectileHitEvent event){
        CruxAttributeInstance instance = CruxAttribute.getInstance(proj, CruxAttribute.PROJECTILE_RICOCHET_BLOCK);
        if(instance == null) return;
        int reflected = CruxPersist.RICOCHET_BLOCK.get(proj, 0);
        int max = (int) instance.getValue();
        if(reflected >= (max-1)) return;

        Vector normal;
        if(event.getHitBlockFace() != null){
            normal = event.getHitBlockFace().getDirection().normalize();
        }else return;

        event.setCancelled(true);

        ProjectileRicochetEvent x = reflectProjectile(proj, normal, reflected,
            CruxAttribute.get(proj, CruxAttribute.PROJECTILE_RICOCHET_BLOCK_VELOCITY_DROP_OFF, .7D),
            CruxPersist.RICOCHET_BLOCK, event.getHitBlock(), event.getHitBlockFace(), event.getHitEntity(),
            null
        );
        if(x.isCancelled()){
            event.setCancelled(false);
            x.getNewProjectile().remove();
            return;
        }
        proj.remove();
    }

    public static ProjectileRicochetEvent reflectProjectile(Projectile proj, Vector normal, int reflected, double dropOff, PersistTag<Integer> tag,
                                                            Block block,
                                                            BlockFace hitFace,
                                                            Entity entity,
                                                            Consumer<Entity> newProjectileConsumer){
        Vector dir = proj.getVelocity();
        Vector v = CruxMath.reflect(dir, normal).multiply(dropOff);

        Location spawn;
        if(entity == null){
            spawn = CruxLoc.shift(proj.getLocation(), v, .2, 0, 0);
        }else spawn = proj.getLocation();

        Entity newProjectile = proj.getWorld().spawnEntity(spawn, proj.getType(), CreatureSpawnEvent.SpawnReason.CUSTOM, x->{
            CruxTag.copyAll(x, proj);
            x.setVelocity(v);

            x.customName(proj.customName());
            x.setSilent(proj.isSilent());
            x.setGravity(proj.hasGravity());
            x.setPersistent(proj.isPersistent());
            x.setGlowing(proj.isGlowing());
            x.setInvisible(proj.isInvisible());
            x.setNoPhysics(proj.hasNoPhysics());

            x.setVisualFire(proj.isVisualFire());
            if(x instanceof ThrowableProjectile newSpawn && proj instanceof ThrowableProjectile old){
                newSpawn.setItem(old.getItem());
                newSpawn.setShooter(old.getShooter());
            }
            if(x instanceof Projectile d){
                d.setShooter(proj.getShooter());
            }
            if(x instanceof AbstractArrow newSpawn && proj instanceof AbstractArrow old){
                newSpawn.setHitSound(old.getHitSound());
                newSpawn.setDamage(old.getDamage());
                newSpawn.setCritical(old.isCritical());
                newSpawn.setItemStack(old.getItemStack());
                newSpawn.setLifetimeTicks(old.getLifetimeTicks());
                newSpawn.setPickupStatus(old.getPickupStatus());
                if(old.getWeapon() != null) newSpawn.setWeapon(old.getWeapon());
                newSpawn.setPierceLevel(old.getPierceLevel());
            }
            tag.set(x, reflected+1);
            CruxPersist.RICOCHET_LAST_HIT_BLOCK_TYPE.set(x, block == null ? null : Crux.handlers().block().getType(block));
            CruxPersist.RICOCHET_LAST_HIT_ENTITY_TYPE.set(x, entity == null ? null : Crux.handlers().entity().getType(entity));
            if(newProjectileConsumer != null) newProjectileConsumer.accept(x);
        });

        ProjectileRicochetEvent event = new ProjectileRicochetEvent(
            proj, newProjectile, v, reflected+1, block, hitFace, entity
        );
        if(!event.callEvent()) return event;

        if(event.getReflectedAmount() != reflected+1){
            tag.set(newProjectile, reflected+1);
        }
        if(!event.getReflectDirection().equals(v)){
            newProjectile.setVelocity(event.getReflectDirection());
        }
        if(!event.getNewProjectile().equals(newProjectile)){
            newProjectile.remove();
        }
        return event;
    }
}
