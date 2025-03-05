package killercreepr.usurvive.core.entity.tickable;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;

public class ActiveAbsorbDamageTickable extends ListenerActiveEntityTickable implements Listener {
    protected final float chance;
    protected final float percentage;
    protected final float max;
    protected final float durabilityDamage;
    protected final Collection<String> damageCauses;
    protected final Collection<Key> damageTypes;

    public ActiveAbsorbDamageTickable(Entity entity, EntityTickable tickable,
                                      CruxSlot slot, float chance, float percentage, float max, float durabilityDamage, Collection<String> damageCauses, Collection<Key> damageTypes) {
        super(entity, tickable, slot);
        this.chance = chance;
        this.percentage = percentage;
        this.max = max;
        this.durabilityDamage = durabilityDamage;
        this.damageCauses = damageCauses;
        this.damageTypes = damageTypes;
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(entity)) return;
        if(!CruxMath.testChance(chance)) return;
        if(damageTypes != null && !damageTypes.contains(event.getDamageSource().getDamageType().key())) return;
        if(damageCauses != null && !damageCauses.contains(event.getCause().toString().toLowerCase())) return;
        double damage = event.getFinalDamage();
        double damageReduced = damage * percentage;
        if(damageReduced > max) damageReduced = max;

        double newDamage = Math.max(event.getDamage() - damageReduced, 0D);
        event.setDamage(newDamage);

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
}
