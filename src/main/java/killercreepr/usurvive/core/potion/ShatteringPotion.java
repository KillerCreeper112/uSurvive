package killercreepr.usurvive.core.potion;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.ActiveEntityPotion;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShatteringPotion extends ActiveEntityPotion<LivingEntity> implements Listener {
    public ShatteringPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier) {
        super(LivingEntity.class, potion, entity, duration, amplifier);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!event.getDamager().equals(entity)) return;
        Entity victim = event.getEntity();
        double armor = CruxAttribute.get(victim, CruxAttribute.ARMOR);
        if(victim instanceof LivingEntity living){
            var instance = living.getAttribute(Attribute.ARMOR);
            if(instance != null){
                armor += instance.getValue();
            }
        }

        if(victim instanceof Player p){
            if(p.isBlocking() && CruxEntityUtil.isDamageSourceBlocked(victim, entity)){
                p.damageItemStack(getShieldSlot(p), calculateShieldDamage());
            }
        }

        if(armor <= 0D) return;

        event.setDamage(calculateMultiplier(armor));
    }

    public EquipmentSlot getShieldSlot(Player p){
        ItemStack item = p.getInventory().getItemInMainHand();
        if(isShield(item)) return EquipmentSlot.HAND;
        return EquipmentSlot.OFF_HAND;
    }

    public boolean isShield(ItemStack item){
        if(CruxItem.isEmpty(item)) return false;
        var data = item.getData(DataComponentTypes.BLOCKS_ATTACKS);
        if(data == null){
            data = item.getType().asItemType().getDefaultData(DataComponentTypes.BLOCKS_ATTACKS);
        }
        if(data == null) return false;
        return true;
    }

    public int calculateShieldDamage(){
        return (amplifier+1) * 2;
    }

    public double calculateMultiplier(double armor){
        double start = .04D + (amplifier * .03D);
        return start * armor;
    }
}
