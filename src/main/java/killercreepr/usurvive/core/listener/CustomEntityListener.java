package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxattributes.core.component.CruxAttributeComponents;
import killercreepr.cruxblocks.api.event.CustomEntityExplodeEvent;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.entity.memory.CruxEntityDamagerTesterHolder;
import killercreepr.usurvive.core.entity.memory.ProjectileHitHolder;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class CustomEntityListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity e = event.getEntity();
        ProjectileHitHolder holder = EntityMemory.getDataHolder(e, ProjectileHitHolder.class);
        if(holder == null) return;
        holder.onProjectileHit(event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCustomEntityExplode(CustomEntityExplodeEvent event) {
        Entity e = event.getEntity();
        if(CruxMob.is(e, USurviveMobs.GUST_TNT)){
            event.clearBlocks();
        }

        String type = CruxTag.get(e, "blast_tempest", PersistentDataType.STRING, null);
        if(type != null){
            event.setCancelled(true);
            onBlastTempest(e, type);
            e.remove();
        }
    }

    public void onBlastTempest(Entity tnt, String type){
        CruxEntity crux = CruxEntity.entity(tnt);
        CruxAttributeContainer attributes = crux.get(CruxAttributeComponents.CRUX_ATTRIBUTES);
        if(attributes == null) return;
        double range = attributes.getValue(CruxAttribute.ATTACK_RANGE);
        new GetEntityNear<>(LivingEntity.class)
            .center(tnt)
            .filter(e ->{
                if(tnt.equals(e)) return false;
                return true;
            })
            .amount(attributes.hasAttribute(CruxAttribute.ATTACK_PIERCE) ?
                (int) (attributes.getValue(CruxAttribute.ATTACK_PIERCE)+1) : null)
            .range(range)
            .find().forEach(hit ->{
                double potency = CruxMath.distanceFalloff(hit, tnt, range, 1.5D);

                DamageSource source = DamageSource.builder(DamageType.EXPLOSION)
                    .withDirectEntity(tnt)
                    .build();
                EntityDamager.entityDamager(hit, tnt)
                    .setSource(source)
                    .setCause(CruxEntityDamageEvent.DamageCause.fromBukkit(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
                    .attackWithMultiplier(potency);
            });
        new ParticleBuilder(Particle.EXPLOSION)
            .offset(range*.5, range*.5, range *.5)
            .count(CruxMath.random(8,12))
            .extra(.1)
            .location(tnt.getLocation())
            .spawn();
        CreateSound.sound(Sound.ENTITY_GENERIC_EXPLODE, net.kyori.adventure.sound.Sound.Source.NEUTRAL, 2.3f, 1.2f)
            .playAt(tnt);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if(CruxMob.is(event.getRemover(), USurviveMobs.GUST_TNT)){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCruxEntityDamage(CruxEntityDamageEvent event) {
        var dmger = event.getDamager();
        var victim = event.getEntity();
        String type = CruxTag.get(dmger, "blast_tempest", PersistentDataType.STRING, null);
        if(type != null){
            CruxEntityDamagerTesterHolder holder = EntityMemory.getOrCreateDataHolder(dmger, CruxEntityDamagerTesterHolder.class, CruxEntityDamagerTesterHolder::new);
            if(holder != null){
                holder.cruxDmged.put(victim.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
        var dmger = event.getHitBy();
        var victim = event.getEntity();
        String type = CruxTag.get(dmger, "blast_tempest", PersistentDataType.STRING, null);
        if(type != null){
            CruxEntityDamagerTesterHolder holder = EntityMemory.getDataHolder(dmger, CruxEntityDamagerTesterHolder.class);
            if(holder != null){
                if(!holder.cruxDmgedWithin(victim, 5)){
                    event.setCancelled(true);
                    return;
                }
                return;
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        var dmger = event.getDamager();
        var victim = event.getEntity();
        String type = CruxTag.get(dmger, "blast_tempest", PersistentDataType.STRING, null);
        if(type != null){
            CruxEntityDamagerTesterHolder holder = EntityMemory.getDataHolder(dmger, CruxEntityDamagerTesterHolder.class);
            if(holder != null){
                if(!holder.cruxDmgedWithin(victim, 5)){
                    event.setCancelled(true);
                    return;
                }
                return;
            }
        }

        if(victim instanceof ArmorStand || !(victim instanceof LivingEntity)){
            if(CruxMob.is(dmger, USurviveMobs.GUST_TNT)){
                event.setDamage(0D);
            }
        }else{
            if(CruxMob.is(dmger, USurviveMobs.GUST_TNT)){
                event.setDamage(event.getDamage()*0.5D);
                Crux.scheduler().runTask(() ->{
                    EntityDamager.entityDamager(victim, dmger)
                        .applyKnockback(CruxAttribute.ATTACK_KNOCKBACK.get(dmger));
                });
            }
        }
    }

}
