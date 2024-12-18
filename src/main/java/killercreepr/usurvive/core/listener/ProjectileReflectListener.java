package killercreepr.usurvive.core.listener;

import killercreepr.crux.core.persistence.CruxPersist;
import killercreepr.crux.core.persistence.PersistTag;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeInstance;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class ProjectileReflectListener implements Listener {
    @EventHandler(ignoreCancelled = true)
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

    public void projectileHitEntity(Projectile proj, ProjectileHitEvent event){
        CruxAttributeInstance instance = CruxAttribute.getInstance(proj, CruxAttribute.PROJECTILE_RICOCHET_ENTITY);
        if(instance == null) return;
        int reflected = CruxPersist.RICOCHET_BLOCK.get(proj, 0);
        int max = (int) instance.getValue();
        if(reflected >= (max-1)) return;

        Vector normal = proj.getLocation().toVector().normalize();

        reflectProjectile(proj, normal, reflected,
            CruxAttribute.get(proj, CruxAttribute.PROJECTILE_RICOCHET_ENTITY_VELOCITY_DROP_OFF, .7D),
            CruxPersist.RICOCHET_ENTITY
        );
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

        reflectProjectile(proj, normal, reflected,
            CruxAttribute.get(proj, CruxAttribute.PROJECTILE_RICOCHET_BLOCK_VELOCITY_DROP_OFF, .7D),
            CruxPersist.RICOCHET_BLOCK
        );
        proj.remove();
    }

    public void reflectProjectile(Projectile proj, Vector normal, int reflected, double dropOff, PersistTag<Integer> tag){
        Vector dir = proj.getVelocity();
        Vector v = CruxMath.reflect(dir, normal).multiply(dropOff);
        proj.getWorld().spawnEntity(proj.getLocation(), proj.getType(), CreatureSpawnEvent.SpawnReason.CUSTOM, x->{
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
            }
            if(x instanceof Projectile d){
                d.setShooter(proj.getShooter());
            }
            tag.set(x, reflected+1);
        });
    }
}
