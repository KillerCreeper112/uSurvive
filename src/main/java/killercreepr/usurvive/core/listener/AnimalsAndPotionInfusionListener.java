package killercreepr.usurvive.core.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.predicate.ItemPredicate;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.usurvive.api.component.InfusedPotions;
import killercreepr.usurvive.api.component.PotionInfusable;
import killercreepr.usurvive.api.event.InfusedItemUpdateEvent;
import killercreepr.usurvive.api.event.PostItemInfusePotionsEvent;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.component.impl.InfusionAttributesBenefit;
import killercreepr.usurvive.core.entity.memory.GoldenItemCauldronHolder;
import killercreepr.usurvive.core.entity.mob.goals.animal.AnimalEnhancedGoal;
import killercreepr.usurvive.core.util.ItemUtil;
import net.kyori.adventure.key.Key;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class AnimalsAndPotionInfusionListener implements Listener {
    protected final ValuesProvider cfg;

    public AnimalsAndPotionInfusionListener(ValuesProvider cfg) {
        this.cfg = cfg;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInfusedItemUpdate(InfusedItemUpdateEvent event) {
        CruxItem item = event.getItem();
        ItemPredicate filter = cfg.APPLICABLE_INFUSION_BENEFITS().value();
        if(filter != null && !filter.test(item.item())) return;

        InfusedPotions newInfused = event.getNewInfusedPotions();
        InfusedPotions oldInfused = event.getCurrentInfusedPotions();
        if(newInfused == null){
            Integer benefit = item.set(USurviveComponents.POTION_INFUSION_DURABILITY_BENEFIT, null);
            if(benefit != null){
                int durability = CruxItem.getMaxDurability(item.item());
                int maxDamage = item.item().getDataOrDefault(DataComponentTypes.MAX_DAMAGE, 0);
                int newMaxDamage = maxDamage - durability;
                if(newMaxDamage < 1){
                    item.item().resetData(DataComponentTypes.MAX_DAMAGE);
                }else item.item().setData(DataComponentTypes.MAX_DAMAGE, newMaxDamage);
            }
            updateAttributes(item, getAllInfusedPotions(oldInfused).size()*-1);
            return;
        }

        int oldPotionAmount = getAllInfusedPotions(oldInfused).size();
        int newPotionAmount = getAllInfusedPotions(newInfused).size();

        if(newPotionAmount >= oldPotionAmount) return; // Greater than should not happen.
        int difference = oldPotionAmount - newPotionAmount;

        int benefit = item.getOrDefault(USurviveComponents.POTION_INFUSION_DURABILITY_BENEFIT, 0);
        int originalDurability = CruxItem.getMaxDurability(item.item()) - benefit;
        int addedBenefitPerPotion = (int) Math.ceil(originalDurability * cfg.INFUSION_DURABILITY_PERCENT().value().floatValue());

        int removeBenefit = addedBenefitPerPotion * difference;

        int newBenefit = benefit - removeBenefit;

        int durability = CruxItem.getMaxDurability(item.item()) - removeBenefit;
        if(durability < 1){
            item.item().resetData(DataComponentTypes.MAX_DAMAGE);
        }else{
            item.item().setData(DataComponentTypes.MAX_DAMAGE, durability);
        }

        if(newBenefit == 0){
            item.set(USurviveComponents.POTION_INFUSION_DURABILITY_BENEFIT, null);
        }else{
            item.set(USurviveComponents.POTION_INFUSION_DURABILITY_BENEFIT, newBenefit);
        }
        updateAttributes(item, difference * -1);
    }

    public double getValue(ItemAttributeModifiers mods, Attribute attribute){
        if(mods == null) return 0D;
        for (ItemAttributeModifiers.Entry entry : mods.modifiers()) {
            if(entry.attribute() == attribute) return entry.modifier().getAmount();
        }
        return 0D;
    }

    public void updateAttributes(CruxItem item, int difference){
        if(difference == 0) return;
        Map<Key, Double> benefitsPer = cfg.INFUSION_ATTRIBUTE_BENEFITS().value();
        if(benefitsPer == null || benefitsPer.isEmpty()) return;
        CruxSlotGroup group = ItemUtil.getDefaultSlotGroup(item.item());
        EquipmentSlotGroup equipGroup = AttributeBukkitAdaptor.adapt(group);
        if(equipGroup == null){
            Crux.log(Level.WARNING, "No EquipmentSlotGroup found from " + group + "!");
            return;
        }
        Map<Key, Double> attributeMap;
        InfusionAttributesBenefit benefit = item.get(USurviveComponents.POTION_INFUSION_ATTRIBUTES_BENEFIT);
        if(benefit == null && difference < 0) return;
        ItemAttributeModifiers attributes = item.item().getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if(benefit == null){
            benefit = new InfusionAttributesBenefit(new HashMap<>(), attributes ==null);
        }
        attributeMap = benefit.getAttributes();

        ItemAttributeModifiers defaultData = benefit.isKeepDefaults() ? item.item().getType().getDefaultData(DataComponentTypes.ATTRIBUTE_MODIFIERS) : null;

        benefitsPer.forEach((key, amount) ->{
            double current = attributeMap.getOrDefault(key, 0D);
            double toAdd = (amount * difference);

            double total = current + toAdd;
            if(total == 0D){
                attributeMap.remove(key);
                return;
            }
            attributeMap.put(key, total);
        });
        String modKeyID = "potion_infusion_attribute/" + group.key().value();
        if(attributeMap.isEmpty()){
            item.set(USurviveComponents.POTION_INFUSION_ATTRIBUTES_BENEFIT, null);
            if(benefit.isKeepDefaults()){
                item.item().resetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
                return;
            }
            ItemAttributeModifiers. Builder builder = ItemAttributeModifiers.itemAttributes();
            if(attributes != null){
                attributes.modifiers().forEach(entry ->{
                    if(entry.modifier().getKey().getKey().equalsIgnoreCase(modKeyID)) return;
                    builder.addModifier(entry.attribute(), entry.modifier(), entry.getGroup());
                });
                item.item().setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder);
            }else item.item().resetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            return;
        }
        ItemAttributeModifiers. Builder builder = ItemAttributeModifiers.itemAttributes();
        if(attributes != null){
            attributes.modifiers().forEach(entry ->{
                if(entry.modifier().getKey().getKey().equalsIgnoreCase(modKeyID)) return;
                builder.addModifier(entry.attribute(), entry.modifier(), entry.getGroup());
            });
        }

        attributeMap.forEach((key, value) ->{
            Attribute attribute = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).get(key);
            if(attribute== null){
                Crux.log(Level.WARNING, "Minecraft attribute of " + key + " not found!");
                return;
            }
            builder.addModifier(attribute, new AttributeModifier(Crux.key(modKeyID),
                getValue(defaultData, attribute) + value,
                AttributeModifier.Operation.ADD_NUMBER,
                equipGroup));
        });
        item.item().setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder);
        item.set(USurviveComponents.POTION_INFUSION_ATTRIBUTES_BENEFIT, benefit);
    }


    @EventHandler(ignoreCancelled = true)
    public void onPostItemInfusePotions(PostItemInfusePotionsEvent event) {
        CruxItem result = event.getResult();
        ItemPredicate filter = cfg.APPLICABLE_INFUSION_BENEFITS().value();
        if(filter != null && !filter.test(result.item())) return;
        InfusedPotions infusedPotions = result.get(USurviveComponents.INFUSED_POTIONS);
        if(infusedPotions==null) return;

        //Collection<PotionEffect> allPotions = getAllInfusedPotions(infusedPotions);
        int difference = event.getPotions().size();
        int benefit = result.getOrDefault(USurviveComponents.POTION_INFUSION_DURABILITY_BENEFIT, 0);
        int originalDurability = CruxItem.getMaxDurability(result.item()) - benefit;
        int addedBenefitPerPotion = (int) Math.ceil(originalDurability * cfg.INFUSION_DURABILITY_PERCENT().value().floatValue());
        int addedBenefit = addedBenefitPerPotion * difference;
        if(addedBenefit != 0){
            result.set(USurviveComponents.POTION_INFUSION_DURABILITY_BENEFIT, benefit + addedBenefit);
            int durability = CruxItem.getMaxDurability(result.item()) + addedBenefit;
            result.item().setData(DataComponentTypes.MAX_DAMAGE, durability);
        }
        updateAttributes(result, difference);
    }

    public Collection<PotionEffect> getAllInfusedPotions(InfusedPotions potions){
        Collection<PotionEffect> list = new ArrayList<>();
        potions.getInfusedPotions().forEach(group -> list.addAll(group.getPotionEffects()));
        return list;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for(Entity e : event.getEntities()){
            if(!(e instanceof Animals a)) continue;
            loadAnimal(a);
        }
    }

    public void onItemSpawn(Item item){
        CruxItem crux = CruxItem.wrap(item.getItemStack());
        PotionInfusable infused = crux.getOrDefaultData(USurviveComponents.POTION_INFUSABLE);
        if(infused == null || infused.getMaxInfusions() < 1) return;

        EntityMemory.getOrCreateDataHolder(item, GoldenItemCauldronHolder.class, GoldenItemCauldronHolder::new);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity e = event.getEntity();
        if(e instanceof Item ee){
            onItemSpawn(ee);
            return;
        }
        if(!(e instanceof Animals a)) return;
        loadAnimal(a);
    }

    public void loadAnimal(@NotNull Animals a){
        /*CruxMob mob = NotOkayMobs.getVanilla(a.getClass());
        if(mob != null){
            mob.load(a);
            return;
        }*/

        CruxGoalUtil.addIfNotPresent(a, AnimalEnhancedGoal.class, 2, () -> new AnimalEnhancedGoal(a));
        /*if(Bukkit.getMobGoals().getGoal(a, AnimalEnhancedGoal.KEY) != null) return;
        Bukkit.getMobGoals().addGoal(a, 2, new AnimalEnhancedGoal(a));*/
    }
}
