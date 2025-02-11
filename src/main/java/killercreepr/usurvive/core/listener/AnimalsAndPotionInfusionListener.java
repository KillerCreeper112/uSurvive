package killercreepr.usurvive.core.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.predicate.ItemPredicate;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.usurvive.api.component.InfusedPotions;
import killercreepr.usurvive.api.component.PotionInfusable;
import killercreepr.usurvive.api.event.InfusedItemUpdateEvent;
import killercreepr.usurvive.api.event.PostItemInfusePotionsEvent;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.entity.memory.GoldenItemCauldronHolder;
import killercreepr.usurvive.core.entity.mob.goals.animal.AnimalEnhancedGoal;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

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
    }

    public void updateAttributes(CruxItem item, int difference){

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
