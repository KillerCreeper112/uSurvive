package killercreepr.usurvive.core.potion;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.SimpleActivePotion;
import org.bukkit.Sound;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class EludingPotion extends SimpleActivePotion implements Listener {
    public EludingPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(potion, entity, duration, amplifier);
    }

    //protected final Map<UUID, Long> cruxed = new HashMap<>();
    @EventHandler(ignoreCancelled = true)
    public void onCruxEntityDamage(CruxEntityDamageEvent event) {
        if(!entity.equals(event.getVictim())) return;
        if(!(event.getDamager() instanceof Projectile dmger)) return;

        //cruxed.put(dmger.getUniqueId(), System.currentTimeMillis());

        double multiplier = Math.max(1D - ((amplifier+1) * 0.25D), 0D);
        event.setDmg(event.getDmg() * multiplier);
        event.setKb(event.getKb() * multiplier);
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if(!entity.equals(event.getHitEntity())) return;
        if(event.getHitBlock() != null) return;
        event.setCancelled(true);

        Entity p = event.getEntity();

        DamageType type = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE)
                .get(p.getType().key());
        EntityDamager.entityDamager(entity, p)
            .setSource(DamageSource.builder(type == null ? DamageType.MOB_PROJECTILE : type)
                .withCausingEntity(EntityDamager.getOwner(p)).withDirectEntity(p).build())
            .setCause(CruxEntityDamageEvent.DamageCause.PROJECTILE)
            .attack();

        Vector vel = p.getLocation().toVector().subtract(CruxEntityUtil.getEyeOrLocation(entity).toVector())
            .multiply((amplifier+1) * 0.5);

        vel.setY(vel.getY() + 0.5);

        p.setVelocity(vel);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ARMOR_STAND_HIT, 1f, 1.6f);
    }

}
