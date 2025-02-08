package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.usurvive.api.component.InfusedPotionGroup;
import killercreepr.usurvive.api.component.InfusedPotions;
import killercreepr.usurvive.core.component.USurviveComponents;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemPotionEffectsHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("infused_potions");
    public ItemPotionEffectsHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }

    public ItemPotionEffectsHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    //potion type key -> infusion
    protected final ActiveInfusionHandler activeInfusions = new ActiveInfusionHandler();

    public CruxItem getItem(EntityEquipment equipment, CruxSlot slot){
        EquipmentSlot equipSlot = AttributeBukkitAdaptor.adapt(slot);
        if(equipSlot == null) return null;
        ItemStack item = equipment.getItem(equipSlot);
        if(CruxItem.isEmpty(item)) return null;
        return CruxItem.wrap(item);
    }

    public ActiveInfusion applyInfusion(@NotNull LivingEntity e, CruxItem item, CruxSlot slot, InfusedPotions infusedPotions){
        Map<Key, Collection<PotionEffect>> applied = new HashMap<>();
        infusedPotions.getInfusedPotions().forEach(group ->{
            if(!group.getSlot().test(slot)) return;
            group.getPotionEffects().forEach(pot ->{
                if(activeInfusions.hasAppliedPotionType(pot.getType()) || e.hasPotionEffect(pot.getType())) return;
                Crux.scheduler().runTask(() -> e.addPotionEffect(pot));
                applied.computeIfAbsent(group.key(), dd -> new HashSet<>()).add(pot);
            });
        });
        if(applied.isEmpty()) return null;
        return new ActiveInfusion(applied, item, slot);
    }

    public void tickSlot(LivingEntity e, EntityEquipment equipment, CruxSlot slot){
        EquipmentSlot equipSlot = AttributeBukkitAdaptor.adapt(slot);
        if(equipSlot==null) return;
        ItemStack item = equipment.getItem(equipSlot);
        if(CruxItem.isEmpty(item)) return;
        CruxItem crux = CruxItem.wrap(item);
        InfusedPotions infusedPotions = crux.get(USurviveComponents.INFUSED_POTIONS);
        if(infusedPotions == null) return;
        ActiveInfusion infusion = applyInfusion(e, crux, slot, infusedPotions);
        if(infusion==null) return;
        activeInfusions.addInfusion(infusion);
    }

    public void tickEquipment(LivingEntity e, EntityEquipment equipment){
        for(CruxSlot slot : CruxSlotGroup.ARMOR){
            tickSlot(e, equipment, slot);
        }
        for(CruxSlot slot : CruxSlotGroup.HAND){
            tickSlot(e, equipment, slot);
        }
    }

    @Override
    public void tick(@NotNull Entity entity) {
        if(!(entity instanceof LivingEntity l)) return;
        EntityEquipment equipment = l.getEquipment();
        if(equipment==null) return;
        tickEquipment(l, equipment);
        tickActiveInfusions(l, equipment);
    }

    public void tickActiveInfusions(LivingEntity e, EntityEquipment equipment){
        for(ActiveInfusion infusion : new HashSet<>(activeInfusions.getActiveInfusions())){
            CruxItem item = getItem(equipment, infusion.slot());
            InfusedPotions infused = item == null ? null : item.get(USurviveComponents.INFUSED_POTIONS);
            if(item == null || infused == null){
                activeInfusions.removeInfusionBySlot(infusion.slot());
                return;
            }
            Collection<InfusedPotionGroup> newGroups = new HashSet<>(infused.getInfusedPotions());
            infusion.potions().forEach((groupKey, effects) ->{
                InfusedPotionGroup group = infused.getInfusedPotion(groupKey);
                if(group == null) return;
                List<PotionEffect> newEffects = new ArrayList<>(group.getPotionEffects());
                effects.forEach(pot ->{
                    PotionEffect active = e.getPotionEffect(pot.getType());
                    newEffects.removeIf(d -> d.getType().equals(pot.getType()));
                    if(active == null){
                        return;
                    }
                    newEffects.add(active);
                });
                InfusedPotionGroup newGroup = group.withPotionEffects(newEffects);
                newGroups.add(newGroup);
            });
            item.set(USurviveComponents.INFUSED_POTIONS, infused.withInfusedPotions(newGroups));
        }
    }

    public record ActiveInfusion(Map<Key, Collection<PotionEffect>> potions, CruxItem item, CruxSlot slot){
        public void merge(ActiveInfusion infusion){
            infusion.potions().forEach((k, p) ->{
                potions.computeIfAbsent(k, (e) -> new HashSet<>()).addAll(p);
            });
        }
    }

    public static class ActiveInfusionHandler {
        protected final Map<Key, ActiveInfusion> potionToActiveInfusions = new HashMap<>();
        protected final Map<CruxSlot, ActiveInfusion> slotToActiveInfusions = new HashMap<>();

        public Collection<ActiveInfusion> getActiveInfusions(){
            return potionToActiveInfusions.values();
        }

        public boolean hasAppliedPotionType(PotionEffectType type){
            return potionToActiveInfusions.containsKey(type.key());
        }

        public void addInfusion(ActiveInfusion infusion){
            ActiveInfusion active = getInfusionBySlot(infusion.slot());
            if(active != null){
                active.merge(infusion);
                return;
            }

            slotToActiveInfusions.put(infusion.slot(), infusion);
            infusion.potions().values().forEach(potions ->{
                potions.forEach(pot ->{
                    potionToActiveInfusions.put(pot.getType().key(), infusion);
                });
            });
        }

        public ActiveInfusion removeInfusionBySlot(CruxSlot slot){
            ActiveInfusion removed = slotToActiveInfusions.remove(slot);
            if(removed == null) return null;
            removed.potions().values().forEach(potions ->{
                potions.forEach(pot ->{
                    potionToActiveInfusions.remove(pot.getType().key());
                });
            });
            onRemoved(removed);
            return removed;
        }

        public ActiveInfusion removeInfusionByPotionType(Key key){
            ActiveInfusion removed = potionToActiveInfusions.remove(key);
            if(removed == null) return null;
            slotToActiveInfusions.remove(removed.slot());
            onRemoved(removed);
            return removed;
        }

        public void onRemoved(ActiveInfusion infusion){

        }

        public ActiveInfusion getInfusionBySlot(CruxSlot slot){
            return slotToActiveInfusions.get(slot);
        }

        public ActiveInfusion getInfusionByPotionType(PotionEffectType type){
            return getInfusionByPotionType(type.key());
        }

        public ActiveInfusion getInfusionByPotionType(Key key){
            return potionToActiveInfusions.get(key);
        }
    }
}
