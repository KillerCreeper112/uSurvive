package killercreepr.usurvive.core.charm;

import killercreepr.crux.api.block.predicate.BlockPredicate;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.paper.ItemHolder;
import killercreepr.cruxblocks.api.event.CruxBlockBreakEvent;
import killercreepr.cruxcharms.api.charm.Charm;
import killercreepr.cruxcharms.api.charm.type.BlockDropItemCharm;
import killercreepr.cruxcharms.api.charm.type.CruxBlockBreakCharm;
import killercreepr.cruxcharms.core.component.charm.SimpleCharm;
import killercreepr.cruxentityblocks.api.EntityBlockManager;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MineBonusCharm extends SimpleCharm implements BlockDropItemCharm, CruxBlockBreakCharm {
    protected final float chance;
    protected final NumberProvider itemEffectAmount;
    protected final NumberProvider dropAmount;
    protected final BlockPredicate predicate;
    protected final boolean includePlayerPlaced;

    public MineBonusCharm(Key key, String name, ItemHolder item, boolean useSlot,
                          float chance, NumberProvider itemEffectAmount, NumberProvider dropAmount, BlockPredicate predicate, boolean includePlayerPlaced) {
        super(key, name, item, useSlot);
        this.chance = chance;
        this.itemEffectAmount = itemEffectAmount;
        this.dropAmount = dropAmount;
        this.predicate = predicate;
        this.includePlayerPlaced = includePlayerPlaced;
    }

    public boolean isIncludePlayerPlaced() {
        return includePlayerPlaced;
    }

    public NumberProvider getItemEffectAmount() {
        return itemEffectAmount;
    }

    public float getChance() {
        return chance;
    }

    public NumberProvider getDropAmount() {
        return dropAmount;
    }

    public BlockPredicate getPredicate() {
        return predicate;
    }

    @Override
    public Charm withItem(@Nullable ItemHolder itemHolder) {
        return new MineBonusCharm(key, name, item, useSlot, chance, itemEffectAmount, dropAmount, predicate,
            includePlayerPlaced);
    }

    @Override
    public void onBlockDropItem(@NotNull BlockDropItemEvent event) {
        if(event.getItems().isEmpty()) return;
        if(!CruxMath.testChance(chance)) return;
        BlockState state = event.getBlockState();
        if(predicate != null && !predicate.test(Crux.handlers().block().getByBlockState(state))) return;

        BlockData data = state.getBlockData();
        if(data instanceof Ageable a){
            if(a.getAge() < a.getMaximumAge()) return;
        }else{
            if(!includePlayerPlaced){
                if(EntityBlockManager.entityBlockManager().wasPlacedByEntity(event.getBlock())) return;
            }
        }
        int effectAmount = itemEffectAmount.value().intValue();
        if(effectAmount < 1) return;
        Location spawn = event.getBlock().getLocation().toCenterLocation();
        List<Item> list = new ArrayList<>(event.getItems());

        while(effectAmount > 0){
            effectAmount--;
            Item drop = CruxCollection.getRandom(list);
            if(drop == null) break;
            list.remove(drop);

            int amount = dropAmount.value().intValue();
            if(amount < 1) continue;

            ItemStack i = drop.getItemStack();
            int maxStack = CruxItem.getMaxStackSize(i);
            while(amount > 0){
                int give = Math.min(maxStack, amount);
                amount -= give;

                i = i.clone();
                i.setAmount(give);
                event.getItems().add(spawn.getWorld().dropItem(spawn, i));
            }

            if(list.isEmpty()) break;
        }
    }

    @Override
    public void onCruxBlockBreak(@NotNull CruxBlockBreakEvent event) {
        Collection<ItemStack> dropList = event.getDrops();
        if(dropList == null) return;
        if(dropList.isEmpty()) return;

        if(!CruxMath.testChance(chance)) return;
        var ctx = event.getContext();
        if(predicate != null && !predicate.test(Crux.handlers().block().getBlock(ctx.getBlock()))) return;

        BlockData data = ctx.getBlock().getBlockData();
        if(data instanceof Ageable a){
            if(a.getAge() < a.getMaximumAge()) return;
        }else{
            if(!includePlayerPlaced){
                if(EntityBlockManager.entityBlockManager().wasPlacedByEntity(ctx.getBlock())) return;
            }
        }
        int effectAmount = itemEffectAmount.value().intValue();
        if(effectAmount < 1) return;
        List<ItemStack> list = new ArrayList<>(dropList);

        while(effectAmount > 0){
            effectAmount--;
            ItemStack drop = CruxCollection.getRandom(list);
            if(drop == null) break;
            list.remove(drop);

            int amount = dropAmount.value().intValue();
            if(amount < 1) continue;

            ItemStack i = drop;
            int maxStack = CruxItem.getMaxStackSize(i);
            while(amount > 0){
                int give = Math.min(maxStack, amount);
                amount -= give;

                i = i.clone();
                i.setAmount(give);
                dropList.add(i);
            }

            if(list.isEmpty()) break;
        }
    }
}
