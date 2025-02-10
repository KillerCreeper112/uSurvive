package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.usurvive.api.component.InfusedPotionGroup;
import killercreepr.usurvive.api.component.InfusedPotions;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.component.USurviveComponents;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntityInfusedPotionsHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("infused_potions");
    public EntityInfusedPotionsHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }

    public EntityInfusedPotionsHolder(@NotNull EntityMemory parent) {
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

    @Override
    public void onMemoryUnload(@NotNull Entity e) {
        super.onMemoryUnload(e);
        if(!(e instanceof LivingEntity l)) return;
    }

    protected int tick = 0;
    @Override
    public void tick(@NotNull Entity entity) {
        tick++;
        if(tick < 2) return;
        tick = 0;
        if(!(entity instanceof LivingEntity l)) return;
        EntityEquipment equipment = l.getEquipment();
        if(equipment==null) return;

        Crux.scheduler().runTask(() ->{
            if(!CruxEntityUtil.isValid(entity)) return;

            Map<Key, PotionEffect> lastTick = new HashMap<>();
            activeInfusions.activeInfusionsByPotionType.forEach((key, infusion) ->{
                lastTick.put(key, infusion.getPotionByType(key));
            });

            for(CruxSlot slot : CruxSlotGroup.ARMOR){
                CruxItem item = getItem(equipment, slot);
                if(item == null){
                    activeInfusions.removeInfusion(slot);
                    continue;
                }
                InfusedPotions infusedPotions = item.getOrDefaultData(USurviveComponents.INFUSED_POTIONS);
                if(infusedPotions==null){
                    activeInfusions.removeInfusion(slot);
                    continue;
                }

                List<InfusedPotionGroup> newGroups = new ArrayList<>();
                boolean changed = false;
                for (InfusedPotionGroup group : infusedPotions.getInfusedPotions()) {
                    if(!group.getSlot().test(slot)){
                        newGroups.add(group);
                        continue;
                    }
                    List<PotionEffect> newGroupPotions = new ArrayList<>();
                    for (PotionEffect groupPotion : group.getPotionEffects()) {
                        Key typeKey = groupPotion.getType().key();
                        if(activeInfusions.hasPotion(typeKey)){
                            ActiveInfusion current = activeInfusions.getByPotionType(typeKey);
                            if(current.getSlot() != slot){
                                continue;
                            }
                            PotionEffect currentInfusion = current.getPotionByType(typeKey);
                            if(!currentInfusion.equals(groupPotion)){
                                newGroupPotions.add(groupPotion);
                                continue;
                            }

                            PotionEffect entityCurrent = l.getPotionEffect(groupPotion.getType());
                            if(entityCurrent == null || entityCurrent.getAmplifier() != groupPotion.getAmplifier()){
                                activeInfusions.removeInfusion(current);
                                changed = true;
                                newGroupPotions.add(groupPotion);
                                continue;
                            }
                            int duration = entityCurrent.getDuration() - 2;
                            PotionEffect updatedPotion = entityCurrent.withDuration(duration);
                            current.updatePotion(updatedPotion);
                            if(duration > 0) newGroupPotions.add(updatedPotion);
                            changed = true;
                            continue;
                        }
                        if(l.hasPotionEffect(groupPotion.getType())){
                            newGroupPotions.add(groupPotion);
                            continue;
                        }
                        if(!l.addPotionEffect(groupPotion)){
                            newGroupPotions.add(groupPotion);
                            continue;
                        }
                        int duration = groupPotion.getDuration() - 2;
                        PotionEffect updatedPotion = groupPotion.withDuration(duration);
                        ActiveInfusion activeInfusion = new ActiveInfusion(activeInfusions, new HashMap<>(), slot).add();
                        activeInfusion.addPotion(updatedPotion);
                        if(duration > 0) newGroupPotions.add(updatedPotion);
                        changed = true;
                    }
                    newGroups.add(group.withPotionEffects(newGroupPotions));
                }
                if(!changed) continue;

                infusedPotions = infusedPotions.withInfusedPotions(newGroups);
                item.set(USurviveComponents.INFUSED_POTIONS, infusedPotions);
            }

            for(PotionEffect potion : l.getActivePotionEffects()){
                Key typeKey = potion.getType().key();
                if(activeInfusions.hasPotion(typeKey)) continue;
                PotionEffect last = lastTick.get(typeKey);
                if(last != null && last.getAmplifier() == potion.getAmplifier()){
                    l.removePotionEffect(potion.getType());
                }
            }
        });
    }

    public static class ActiveInfusion{
        protected final ActiveInfusionHandler handler;
        protected final Map<Key, PotionEffect> potions;
        protected final CruxSlot slot;

        public ActiveInfusion(ActiveInfusionHandler handler, Map<Key, PotionEffect> potions, CruxSlot slot) {
            this.handler = handler;
            this.potions = potions;
            this.slot = slot;
        }


        public CruxSlot getSlot() {
            return slot;
        }

        public ActiveInfusion add(){
            handler.addInfusion(this);
            return this;
        }

        public void addPotion(PotionEffect effect){
            if(effect.getDuration() < 1){
                removePotion(effect.getType().key());
                return;
            }
            potions.put(effect.getType().key(), effect);
            handler.addPotion(effect.getType().key(), this);
        }

        public PotionEffect getPotionByType(Key type){
            return potions.get(type);
        }

        /**
         * @return Null if this infusion was removed from the handler.
         */
        public ActiveInfusion removePotion(Key type){
            potions.remove(type);
            handler.removePotion(type);
            if(isEmpty()){
                handler.removeInfusion(this);
                return null;
            }
            return this;
        }

        public void updatePotion(PotionEffect effect){
            if(effect.getDuration() < 1){
                removePotion(effect.getType().key());
            } else addPotion(effect);
        }

        public Collection<PotionEffect> getPotions(){
            return potions.values();
        }

        public boolean isEmpty(){
            return potions.isEmpty();
        }
    }

    public static class ActiveInfusionHandler{
        protected final Map<CruxSlot, ActiveInfusion> activeInfusions = new HashMap<>();
        protected final Map<Key, ActiveInfusion> activeInfusionsByPotionType = new HashMap<>();

        public boolean hasPotion(Key type){
            return activeInfusionsByPotionType.containsKey(type);
        }

        public Collection<ActiveInfusion> getActiveInfusions(){
            return activeInfusions.values();
        }

        protected void addPotion(Key type, ActiveInfusion infusion){
            activeInfusionsByPotionType.put(type, infusion);
        }

        protected ActiveInfusion removePotion(Key type){
            return activeInfusionsByPotionType.remove(type);
        }

        protected void addInfusion(ActiveInfusion infusion){
            activeInfusions.put(infusion.slot, infusion);
            infusion.potions.keySet().forEach(key ->{
                activeInfusionsByPotionType.put(key, infusion);
            });
        }

        public ActiveInfusion removeInfusion(CruxSlot slot){
            ActiveInfusion removed = activeInfusions.remove(slot);
            if(removed==null) return null;
            removeInfusion(removed);
            return removed;
        }

        protected void removeInfusion(ActiveInfusion infusion){
            activeInfusions.remove(infusion.slot);
            infusion.potions.keySet().forEach(activeInfusionsByPotionType::remove);
        }

        public ActiveInfusion getBySlot(CruxSlot slot){
            return activeInfusions.get(slot);
        }
        public ActiveInfusion getByPotionType(Key type){
            return activeInfusionsByPotionType.get(type);
        }
        public ActiveInfusion getByPotionType(PotionEffectType type){
            return getByPotionType(type.key());
        }
    }
}
