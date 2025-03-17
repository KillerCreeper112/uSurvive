package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import killercreepr.usurvive.api.entity.player.UPlayer;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ActiveAbsorbNearbyFriendlyDamageTickable extends ListenerActiveEntityTickable implements Listener {
    protected final float chance;
    protected final float percentage;
    protected final float max;
    protected final float durabilityDamage;
    protected final Collection<String> damageCauses;
    protected final Collection<Key> damageTypes;
    protected final float range;

    public ActiveAbsorbNearbyFriendlyDamageTickable(Entity entity, EntityTickable tickable,
                                                    CruxSlot slot, float chance, float percentage, float max, float durabilityDamage, Collection<String> damageCauses, Collection<Key> damageTypes, float range) {
        super(entity, tickable, slot);
        this.chance = chance;
        this.percentage = percentage;
        this.max = max;
        this.durabilityDamage = durabilityDamage;
        this.damageCauses = damageCauses;
        this.damageTypes = damageTypes;
        this.range = range;
    }

    @Override
    public void tick() {
    }

    public ItemStack getItem(){
        if(slot==null) return null;
        if(!(entity instanceof LivingEntity le)) return null;
        EntityEquipment equip = le.getEquipment();
        if(equip == null) return null;
        EquipmentSlot equipSlot = AttributeBukkitAdaptor.adapt(slot);
        if(equipSlot==null) return null;
        return equip.getItem(equipSlot);
    }

    protected final Map<EntityDamageEvent, Double> scheduledDmg = new HashMap<>();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity victim = event.getEntity();
        if(victim.equals(entity)) return;
        if(!entity.getWorld().equals(victim.getWorld())) return;
        if(!(victim instanceof HumanEntity)) return;
        if(!CruxMath.testChance(chance)) return;
        if(damageTypes != null && !damageTypes.contains(event.getDamageSource().getDamageType().key())) return;
        if(damageCauses != null && !damageCauses.contains(event.getCause().toString().toLowerCase())) return;

        var up = UPlayer.getPlayer(entity);
        if(up == null) return;
        if(!up.hasFriend(victim.getUniqueId()) && !up.isApartOfParty(victim.getUniqueId())) return;
        double distanceSquared = victim.getLocation().distanceSquared(entity.getLocation());
        if(distanceSquared > (range * range)) return;

        double damage = event.getFinalDamage();
        double damageReduced = damage * percentage;
        if(damageReduced > max) damageReduced = max;

        double newDamage = Math.max(event.getDamage() - damageReduced, 0D);
        double totalReduced = event.getDamage() - newDamage;
        event.setDamage(newDamage);
        scheduledDmg.put(event, totalReduced);

        int itemDmg = (int)(Math.ceil(damageReduced * durabilityDamage));
        if(itemDmg < 1) return;
        ItemStack item = getItem();
        if(CruxItem.isEmpty(item)) return;
        if(entity instanceof LivingEntity d){
            ItemStack newItem = d.damageItemStack(item, itemDmg);
            if((d.getEquipment() instanceof PlayerInventory)) return;
            d.getEquipment().setItem(AttributeBukkitAdaptor.adapt(slot), newItem);
        }
        //nothing for normal entity
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageHighest(EntityDamageEvent event) {
        Double dmg = scheduledDmg.remove(event);
        if(dmg == null || event.isCancelled()) return;
        Entity dmger;
        if(event instanceof EntityDamageByEntityEvent d){
            dmger = d.getDamager();
        }else dmger = null;

        EntityDamager.entityDamager(entity, dmger)
            .setSource(event.getDamageSource())
            .setCause(CruxEntityDamageEvent.DamageCause.grim(event.getCause()))
            .attack(dmg);
    }

}
