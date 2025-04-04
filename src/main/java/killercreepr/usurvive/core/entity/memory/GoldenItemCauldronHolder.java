package killercreepr.usurvive.core.entity.memory;

import com.destroystokyo.paper.ParticleBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import killercreepr.usurvive.api.component.InfusedPotions;
import killercreepr.usurvive.api.component.PotionInfusable;
import killercreepr.usurvive.api.event.PostItemInfusePotionsEvent;
import killercreepr.usurvive.api.event.PreItemInfusePotionsEvent;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.component.impl.SimpleInfusedPotionGroup;
import killercreepr.usurvive.core.component.impl.SimpleInfusedPotions;
import killercreepr.usurvive.core.util.ItemUtil;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GoldenItemCauldronHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("golden_item/cauldron/infuse_potions");
    public GoldenItemCauldronHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }
    public GoldenItemCauldronHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    protected int tick = 0;
    @Override
    public void tick(@NotNull Entity entity) {
        tick++;
        if(tick < 15) return;
        tick = 0;
        Block b = entity.getLocation().getBlock();
        if(b.getType() != Material.WATER_CAULDRON) return;
        Crux.scheduler().runTask(() -> checkLogic((Item) entity, b));
    }

    public Collection<PotionEffect> getAllInfusedPotions(InfusedPotions potions){
        Collection<PotionEffect> list = new ArrayList<>();
        potions.getInfusedPotions().forEach(group -> list.addAll(group.getPotionEffects()));
        return list;
    }

    public List<PotionEffect> getAllPotions(PotionContents contents){
        List<PotionEffect> list = new ArrayList<>();
        if(contents.potion() != null) list.addAll(contents.potion().getPotionEffects());
        list.addAll(contents.customEffects());
        return list;
    }

    public InfuseResult infusePotions(ItemStack item, List<PotionEffect> potions){
        CruxItem crux = CruxItem.wrap(item);
        PotionInfusable infusable = crux.getOrDefaultData(USurviveComponents.POTION_INFUSABLE);
        if(infusable == null || infusable.getMaxInfusions() < 1) return null;
        InfusedPotions infused = crux.getOrDefaultData(USurviveComponents.INFUSED_POTIONS);
        Collection<PotionEffect> totalPotions = infused == null ? List.of() : getAllInfusedPotions(infused);

        if((totalPotions.size() + potions.size()) > infusable.getMaxInfusions()) return null;

        PreItemInfusePotionsEvent preEvent = new PreItemInfusePotionsEvent(item, potions);
        if(!preEvent.callEvent() || potions.isEmpty()){
            return null;
        }

        CruxSlotGroup slot = ItemUtil.getDefaultSlotGroup(item);
        InfusedPotions newInfused;
        if(infused == null){
            newInfused = new SimpleInfusedPotions(List.of(new SimpleInfusedPotionGroup(potions, slot)));
        }else{
            newInfused = infused.applyToFirstFound(
                testGroup -> testGroup.getSlot().compare(slot),
                found -> found.addPotionEffects(potions),
                () -> new SimpleInfusedPotionGroup(potions, slot)
            );
        }
        crux.set(USurviveComponents.INFUSED_POTIONS, newInfused);
        return new InfuseResult(crux);
    }

    public boolean isValidPotionItem(Item item){
        return isValidPotionItem(item.getItemStack());
    }

    public boolean isValidPotionItem(ItemStack item){
        if(CruxItem.isEmpty(item)) return false;
        PotionContents contents = item.getDataOrDefault(DataComponentTypes.POTION_CONTENTS, item.getType().getDefaultData(DataComponentTypes.POTION_CONTENTS));
        if(contents == null) return false;
        return contents.potion() != null || !contents.customEffects().isEmpty();
    }

    public void checkLogic(Item owner, Block b){
        BoundingBox box = b.getBoundingBox();
        for (Entity near : b.getWorld().getNearbyEntities(box, e -> e instanceof Item i && isValidPotionItem(i))) {
            Item item = (Item) near;
            ItemStack stack = item.getItemStack();
            PotionContents contents = stack.getDataOrDefault(DataComponentTypes.POTION_CONTENTS, stack.getType().getDefaultData(DataComponentTypes.POTION_CONTENTS));
            List<PotionEffect> allPotions = getAllPotions(contents);
            InfuseResult result = infusePotions(owner.getItemStack().clone(), allPotions);
            if(result==null) break;

            PostItemInfusePotionsEvent postEvent = new PostItemInfusePotionsEvent(owner.getItemStack(), allPotions, result.getResult(), owner);
            if(!postEvent.callEvent()) return;

            owner.setItemStack(result.getResult().item());
            onMergeSuccess(owner, near);
            break;
        }
    }

    public void onMergeSuccess(Entity owner, Entity near){
        near.remove();
        owner.setVelocity(new Vector(0, CruxMath.random(.2, .3), 0));
        Block b = owner.getLocation().getBlock();

        if(b.getType() == Material.WATER_CAULDRON && b.getBlockData() instanceof Levelled level){
            int unClamped = level.getLevel()-1;
            int newLevel = CruxMath.clamp(unClamped, level.getMinimumLevel(), level.getMaximumLevel());
            if(newLevel != level.getLevel()){
                level.setLevel(newLevel);
                b.setBlockData(level);
            }else if(unClamped < level.getMinimumLevel()){
                Crux.handlers().block().setType(b, Material.CAULDRON);
            }
        }

        Location spawnLoc = b.getLocation().toCenterLocation().add(0, .5, 0);
        new ParticleBuilder(Particle.WITCH)
            .location(spawnLoc)
            .count(CruxMath.random(8, 15))
            .extra(.1)
            .offset(.1, .1, .1)
            .spawn()
            ;
        int count = CruxMath.random(8, 10);
        while(count > 0){
            count--;
            new ParticleBuilder(Particle.SPLASH)
                .location(spawnLoc.clone().add(
                    CruxMath.random(-.2, .2),
                    0,
                    CruxMath.random(-.2, .2)
                ))
                .count(0)
                .extra(.1)
                .offset(0, 1, 0)
                .spawn()
            ;
        }
        CreateSound.sound(Sound.ENTITY_EVOKER_CAST_SPELL, 1.5f).playAt(owner);
        CreateSound.sound(Sound.BLOCK_BREWING_STAND_BREW, 1.5f).playAt(owner);
        CreateSound.sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f).playAt(owner);
    }

    public static class InfuseResult{
        protected final CruxItem result;

        public InfuseResult(CruxItem result) {
            this.result = result;
        }

        public CruxItem getResult() {
            return result;
        }
    }
}
