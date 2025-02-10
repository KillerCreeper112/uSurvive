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

public class EntityInfusedPotionsHolderCopy extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("infused_potions");
    public EntityInfusedPotionsHolderCopy(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }

    public EntityInfusedPotionsHolderCopy(@NotNull EntityMemory parent) {
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
            active.potions().values().forEach(potions ->{
                potions.values().forEach(pot ->{
                    l.removePotionEffect(pot.getType());
                });
            });
        });
        activeInfusions.clear();
    }

    protected final Map<Key, PotionEffect> activeThisTick = new HashMap<>();
    protected final Map<Key, PotionEffect> activeLastTick = new HashMap<>();
    protected final Map<Key, PotionEffect> applyingEffects = new HashMap<>();
    public ActiveInfusion applyInfusion(@NotNull LivingEntity e, CruxItem item, CruxSlot slot, InfusedPotions infusedPotions){
        //Collection<PotionEffect> toApply = new HashSet<>();
        Map<Key, Map<Key, PotionEffect>> applied = new HashMap<>();
        ActiveInfusion activeInfusion = activeInfusions.getInfusionBySlot(slot);

        if(activeInfusion != null && !activeInfusion.item().item().equals(item.item())){
            activeInfusions.removeInfusionBySlot(slot);
            activeInfusion.potions().values().forEach(potions ->{
                potions.values().forEach(pot -> e.removePotionEffect(pot.getType()));
            });
        }

        infusedPotions.getInfusedPotions().forEach(group ->{
            if(!group.getSlot().test(slot)) return;
            group.getPotionEffects().forEach(pot ->{
                if(activeInfusions.hasAppliedPotionType(pot.getType())){
                    activeThisTick.put(pot.getType().key(), pot);
                    return;
                }
                if(hasPotionEffect(e, pot.getType())) return;
                //toApply.add(pot);
                applied.computeIfAbsent(group.key(), dd -> new HashMap<>()).put(pot.getType().key(), pot);
                activeThisTick.put(pot.getType().key(), pot);
                applyingEffects.put(pot.getType().key(), pot);
            });
        });
        if(applied.isEmpty()) return null;
        return new ActiveInfusion(applied, item, slot, infusedPotions);
    }

    public boolean hasPotionEffect(LivingEntity e, PotionEffectType type){
        return e.hasPotionEffect(type) || applyingEffects.containsKey(type.key());
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
        if(!applyingEffects.isEmpty()){
            /*Crux.scheduler().runTask(() ->{
                e.addPotionEffects(applyingEffects.values());
                applyingEffects.clear();
            });*/
            e.addPotionEffects(applyingEffects.values());
            applyingEffects.clear();
        }
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
            //if(!applyingEffects.isEmpty()) return;
            activeThisTick.clear();
            tickEquipment(l, equipment);

            for(PotionEffect potion : l.getActivePotionEffects()){
                Key typeKey = potion.getType().key();
                PotionEffect activeLastTick = this.activeLastTick.get(typeKey);
                PotionEffect activeNow = activeThisTick.get(typeKey);
                if(activeLastTick != null && activeNow == null){
                    l.removePotionEffect(potion.getType());
                    continue;
                }
                if(activeNow==null) continue;

                if(activeNow.getAmplifier() == potion.getAmplifier() || potion.getDuration() > activeNow.getDuration()) continue;

                activeInfusions.removeInfusionByPotionType(typeKey);
                activeThisTick.remove(typeKey);
                PotionEffect hidden = potion.getHiddenPotionEffect();
                if(hidden != null && hidden.getType() == potion.getType() && hidden.getAmplifier() == activeNow.getAmplifier()){
                    l.removePotionEffect(potion.getType());
                    l.addPotionEffect(new PotionEffect(potion.getType(), potion.getDuration(), potion.getAmplifier(), potion.isAmbient(), potion.hasParticles(), potion.hasIcon()));
                }
            }
            activeLastTick.clear();
            /*if(!activeLastTick.isEmpty()){
                Collection<PotionEffectType> toRemove = new HashSet<>();
                activeLastTick.values().forEach(pot ->{
                    if(!activeThisTick.containsKey(pot.getType().key())){
                        toRemove.add(pot.getType());
                    }
                });
                if(!toRemove.isEmpty()){
                    toRemove.forEach(l::removePotionEffect);*//*Crux.scheduler().runTask(() -> toRemove.forEach(l::removePotionEffect));*//*
                }
                activeLastTick.clear();
            }*/
            activeLastTick.putAll(activeThisTick);

            tickActiveInfusions(l, equipment);
        });
    }

    public PotionEffect getActivePotion(LivingEntity e, PotionEffectType type){
        PotionEffect active = e.getPotionEffect(type);
        if(active != null) return active;
        return applyingEffects.get(type.key());
    }

    public void tickActiveInfusions(LivingEntity e, EntityEquipment equipment){
        for(ActiveInfusion infusion : new HashSet<>(activeInfusions.getActiveInfusions())){
            CruxItem item = infusion.item(); //getItem(equipment, infusion.slot());
            InfusedPotions infused = infusion.infusedPotions(); /*item == null ? null : item.get(USurviveComponents.INFUSED_POTIONS);
            if(item == null || infused == null){
                activeInfusions.removeInfusionBySlot(infusion.slot());
                return;
            }*/
            List<InfusedPotionGroup> newGroups = new ArrayList<>(infused.getInfusedPotions());
            infusion.potions().forEach((groupKey, effects) ->{
                InfusedPotionGroup group = infused.getInfusedPotion(groupKey);
                if(group == null) return;

                List<PotionEffect> newEffects = new ArrayList<>();
                Collection<Key> changed = new HashSet<>();
                group.getPotionEffects().forEach(groupPotion ->{
                    Key typeKey = groupPotion.getType().key();
                    if(changed.contains(typeKey)){
                        newEffects.add(groupPotion);
                        return;
                    }
                    PotionEffect appliedPotion = effects.get(typeKey);
                    if(appliedPotion==null || appliedPotion.getAmplifier() != groupPotion.getAmplifier()){
                        newEffects.add(groupPotion);
                        return;
                    }
                    changed.add(typeKey);
                    PotionEffect active = getActivePotion(e, groupPotion.getType());
                    if(active == null){
                        return;
                    }
                    int duration = active.getDuration()-1;
                    if(duration < 1) return;
                    PotionEffect pot = active.withDuration(duration);
                    newEffects.add(pot);
                    effects.put(typeKey, pot);
                });
                InfusedPotionGroup newGroup = group.withPotionEffects(newEffects);
                newGroups.add(newGroup);

                /*List<PotionEffect> newEffects = new ArrayList<>(group.getPotionEffects());
                effects.forEach(pot ->{
                    PotionEffect active = getActivePotion(e, pot.getType());
                    newEffects.removeIf(d -> d.getType().equals(pot.getType()));
                    if(active == null){
                        return;
                    }
                    int duration = active.getDuration()-1;
                    if(duration < 1) return;
                    newEffects.add(active.withDuration(duration));
                });
                InfusedPotionGroup newGroup = group.withPotionEffects(newEffects);
                newGroups.add(newGroup);*/
            });
            InfusedPotions newInfusion = infused.withInfusedPotions(newGroups);
            item.set(USurviveComponents.INFUSED_POTIONS, newInfusion);
            infusion.infusedPotions(newInfusion);
        }
    }

    public static class ActiveInfusion {
        private final Map<Key, Map<Key, PotionEffect>> potions;
        private final CruxItem item;
        private final CruxSlot slot;
        private InfusedPotions infusedPotions;

        public ActiveInfusion(Map<Key, Map<Key, PotionEffect>> potions, CruxItem item, CruxSlot slot, InfusedPotions infusedPotions) {
            this.potions = potions;
            this.item = item;
            this.slot = slot;
            this.infusedPotions = infusedPotions;
        }

        public Map<Key, Map<Key, PotionEffect>> potions() {
            return potions;
        }

        public CruxItem item() {
            return item;
        }

        public CruxSlot slot() {
            return slot;
        }

        public InfusedPotions infusedPotions() {
            return infusedPotions;
        }

        public ActiveInfusion infusedPotions(InfusedPotions infusedPotions){
            this.infusedPotions = infusedPotions;
            return this;
        }

        public void merge(ActiveInfusion infusion) {
            if (infusion == null) return;
            infusion.potions().forEach((k, p) ->
                potions.computeIfAbsent(k, e -> new HashMap<>()).putAll(p)
            );
        }
    }


    /*public record ActiveInfusion(Map<Key, Map<Key, PotionEffect>> potions, CruxItem item, CruxSlot slot, InfusedPotions infusedPotions){
        public void merge(ActiveInfusion infusion){
            infusion.potions().forEach((k, p) ->{
                potions.computeIfAbsent(k, (e) -> new HashMap<>()).putAll(p);
            });
        }
    }*/

    public static class ActiveInfusionHandler {
        protected final Map<Key, ActiveInfusion> potionToActiveInfusions = new HashMap<>();
        protected final Map<CruxSlot, ActiveInfusion> slotToActiveInfusions = new HashMap<>();

        public void clear(){
            potionToActiveInfusions.clear();
            slotToActiveInfusions.clear();
        }

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
                potions.values().forEach(pot ->{
                    potionToActiveInfusions.put(pot.getType().key(), infusion);
                });
            });
        }

        public ActiveInfusion removeInfusionBySlot(CruxSlot slot){
            ActiveInfusion removed = slotToActiveInfusions.remove(slot);
            if(removed == null) return null;
            removed.potions().values().forEach(potions ->{
                potions.values().forEach(pot ->{
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
