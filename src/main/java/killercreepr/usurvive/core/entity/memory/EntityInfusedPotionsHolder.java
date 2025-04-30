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
import killercreepr.usurvive.api.event.InfusedItemPotionAddToEntityEvent;
import killercreepr.usurvive.api.event.InfusedItemUpdateEvent;
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
        activeInfusions.getActiveInfusions().forEach(active ->{
            active.getPotions().forEach(pot ->{
                l.removePotionEffect(pot.getType());
            });
        });
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
            primaryTick(l, equipment);
        });
    }

    public boolean compareCurrentAndGroupPotion(PotionEffect entityCurrent, PotionEffect groupPotion){
        if(entityCurrent == null) return false;
        if(entityCurrent.getAmplifier() != groupPotion.getAmplifier()) return false;
        if(groupPotion.isInfinite() && entityCurrent.isInfinite()) return true;
        return (entityCurrent.getDuration() - 2) <= groupPotion.getDuration();
    }

    public void tickSlot(LivingEntity l, CruxSlot slot, CruxItem item){
        if(item == null){
            activeInfusions.removeInfusion(slot);
            return;
        }
        InfusedPotions infusedPotions = item.getOrDefaultData(USurviveComponents.INFUSED_POTIONS);
        if(infusedPotions==null){
            activeInfusions.removeInfusion(slot);
            return;
        }

        List<InfusedPotionGroup> newGroups = new ArrayList<>();
        boolean changed = false;
        for (InfusedPotionGroup group : infusedPotions.getInfusedPotions()) {
            if(!group.getSlot().test(slot)){
                newGroups.add(group);
                continue;
            }
            ActiveInfusion activeInfusion = activeInfusions.getBySlot(slot);
            //If it's not the same item, we need to remove the potion effects
            if(activeInfusion != null && !activeInfusion.getItem().item().equals(item.item())){
                activeInfusion.getPotions().forEach(pot ->{
                    l.removePotionEffect(pot.getType());
                });
                activeInfusions.removeInfusion(activeInfusion);
                activeInfusion = null;
            }

            List<PotionEffect> newGroupPotions = new ArrayList<>();
            for (PotionEffect groupPotion : group.getPotionEffects()) {
                Key typeKey = groupPotion.getType().key();
                if(activeInfusions.hasPotion(typeKey)){
                    ActiveInfusion current = activeInfusions.getByPotionType(typeKey);
                    if(current.getSlot() != slot){
                        newGroupPotions.add(groupPotion);
                        continue;
                    }
                    PotionEffect currentInfusion = current.getPotionByType(typeKey);
                    if(!currentInfusion.equals(groupPotion)){
                        newGroupPotions.add(groupPotion);
                        continue;
                    }

                    PotionEffect entityCurrent = l.getPotionEffect(groupPotion.getType());
                    if(!compareCurrentAndGroupPotion(entityCurrent, groupPotion)){
                        activeInfusions.removeInfusion(current);
                        newGroupPotions.add(groupPotion);
                        continue;
                    }
                        /*if(entityCurrent == null || entityCurrent.getAmplifier() != groupPotion.getAmplifier() || (entityCurrent.getDuration()-2) > groupPotion.getDuration()){
                            activeInfusions.removeInfusion(current);
                            changed = true;
                            newGroupPotions.add(groupPotion);
                            continue;
                        }*/
                    if(entityCurrent.isInfinite()){
                        newGroupPotions.add(groupPotion);
                        continue;
                    }
                    int duration = Math.max(entityCurrent.getDuration() - 2, 0);
                    PotionEffect updatedPotion = entityCurrent.withDuration(duration);
                    current.updatePotion(updatedPotion);
                    if(duration > 1) newGroupPotions.add(updatedPotion);
                    changed = true;
                    continue;
                }
                if(l.hasPotionEffect(groupPotion.getType())){
                    newGroupPotions.add(groupPotion);
                    continue;
                }

                InfusedItemPotionAddToEntityEvent event = new InfusedItemPotionAddToEntityEvent(l, item, slot, groupPotion);
                if(!event.callEvent()){
                    newGroupPotions.add(groupPotion);
                    continue;
                }

                if(!l.addPotionEffect(groupPotion)){
                    newGroupPotions.add(groupPotion);
                    continue;
                }
                PotionEffect updatedPotion = groupPotion.isInfinite() ? groupPotion : groupPotion.withDuration(Math.max(groupPotion.getDuration() - 2, 0));
                activeInfusion = activeInfusion == null ? new ActiveInfusion(activeInfusions, new HashMap<>(), slot, item).add() : activeInfusion;
                activeInfusion.addPotion(updatedPotion);
                if(updatedPotion.getDuration() > 1 || updatedPotion.isInfinite()) newGroupPotions.add(updatedPotion);
                changed = !groupPotion.isInfinite();
            }
            if(newGroupPotions.isEmpty()) continue;
            InfusedPotionGroup newGroup = group.withPotionEffects(newGroupPotions);
            newGroups.add(newGroup);
        }
        if(!changed) return;

        InfusedPotions newInfusedPotions = newGroups.isEmpty() ? null : infusedPotions.withInfusedPotions(newGroups);
        InfusedItemUpdateEvent updateEvent = new InfusedItemUpdateEvent(l, item, slot, infusedPotions, newInfusedPotions);
        if(!updateEvent.callEvent()) return;

        item.set(USurviveComponents.INFUSED_POTIONS, updateEvent.getNewInfusedPotions());
    }

    public void primaryTick(LivingEntity l, EntityEquipment equipment){
        Map<Key, PotionEffect> lastTick = new HashMap<>();
        activeInfusions.activeInfusionsByPotionType.forEach((key, infusion) ->{
            lastTick.put(key, infusion.getPotionByType(key));
        });

        for(CruxSlot slot : CruxSlotGroup.ARMOR){
            CruxItem item = getItem(equipment, slot);
            tickSlot(l, slot, item);
        }

        for(PotionEffect potion : l.getActivePotionEffects()){
            Key typeKey = potion.getType().key();
            if(activeInfusions.hasPotion(typeKey)) continue;
            PotionEffect last = lastTick.get(typeKey);
            if(last==null) continue;
            if(last.getAmplifier() == potion.getAmplifier() && compareDuration(potion, last)){
                l.removePotionEffect(potion.getType());
                continue;
            }
            PotionEffect hidden = potion.getHiddenPotionEffect();
            if(hidden == null) continue;
            if(hidden.getType() != potion.getType()) continue;
            if(last.getAmplifier() == hidden.getAmplifier() && compareDuration(hidden, last)){
                l.removePotionEffect(hidden.getType());
                l.addPotionEffect(new PotionEffect(hidden.getType(), potion.getDuration(),
                    potion.getAmplifier(),
                    potion.isAmbient(),
                    potion.hasParticles(),
                    potion.hasIcon()));
            }
        }
    }

    public boolean compareDuration(PotionEffect potion, PotionEffect last){
        return (potion.isInfinite() && last.isInfinite()) || (potion.getDuration()-2) <= last.getDuration();
    }

    public static class ActiveInfusion{
        protected final ActiveInfusionHandler handler;
        protected final Map<Key, PotionEffect> potions;
        protected final CruxSlot slot;
        protected final CruxItem item;

        public ActiveInfusion(ActiveInfusionHandler handler, Map<Key, PotionEffect> potions, CruxSlot slot, CruxItem item) {
            this.handler = handler;
            this.potions = potions;
            this.slot = slot;
            this.item = item;
        }

        public ActiveInfusionHandler getHandler() {
            return handler;
        }

        public CruxItem getItem() {
            return item;
        }

        public CruxSlot getSlot() {
            return slot;
        }

        public ActiveInfusion add(){
            handler.addInfusion(this);
            return this;
        }

        public void addPotion(PotionEffect effect){
            if(effect.getDuration() < 2 && !effect.isInfinite()){
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
            if(effect.getDuration() < 2 && !effect.isInfinite()){
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
