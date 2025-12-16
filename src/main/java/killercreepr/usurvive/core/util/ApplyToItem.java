package killercreepr.usurvive.core.util;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.usurvive.core.data.Pack;
import killercreepr.usurvive.core.entity.memory.ItemPackHolder;
import org.bukkit.block.BlockState;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ApplyToItem {
    public static ApplyToItem applyToItem(ItemApplier<ItemStack> applier){
        return new ApplyToItem(applier);
    }
    public static ApplyToItem applyToItem(Consumer<ItemStack> applier){
        return new ApplyToItem(item ->{
            applier.accept(item);
            return Result.DEFAULT;
        });
    }

    public static ApplyToItem removeItems(Predicate<ItemStack> filter, int amount){
        var removed = new AtomicInteger();
        return applyToItem(item ->{
            if(filter != null && !filter.test(item)) return Result.DEFAULT;
            int canRemove = Math.min(amount - removed.get(), item.getAmount());
            if(canRemove < 1) return Result.DEFAULT;

            item.setAmount(item.getAmount() - canRemove);
            if(removed.addAndGet(canRemove) >= amount) return Result.STOP_ITERATING;

            return Result.DEFAULT;
        });
    }

    public static int removeItemsApply(Predicate<ItemStack> filter, int amount, Consumer<ApplyToItem> consumer){
        var removed = new AtomicInteger();
        consumer.accept(
            applyToItem(item ->{
                if(filter != null && !filter.test(item)) return Result.DEFAULT;
                int canRemove = Math.min(amount - removed.get(), item.getAmount());
                if(canRemove < 1) return Result.DEFAULT;

                item.setAmount(item.getAmount() - canRemove);
                if(removed.addAndGet(canRemove) >= amount) return Result.STOP_ITERATING;

                return Result.DEFAULT;
            })
        );
        return removed.get();
    }

    public static boolean hasItems(Predicate<ItemStack> filter, int amount, Consumer<ApplyToItem> consumer){
        return getItemCount(filter, amount, consumer) >= amount;
    }

    public static int getItemCount(Predicate<ItemStack> filter, int maxAmount, Consumer<ApplyToItem> consumer){
        var got = new AtomicInteger();
        consumer.accept(
            applyToItem(item ->{
                if(filter != null && !filter.test(item)) return Result.DEFAULT;
                if(got.addAndGet(item.getAmount()) >= maxAmount) return Result.STOP_ITERATING;
                return Result.DEFAULT;
            })
        );
        return got.get();
    }

    protected final ItemApplier<ItemStack> itemConsumer;

    public ApplyToItem(ItemApplier<ItemStack> itemConsumer) {
        this.itemConsumer = itemConsumer;
    }

    public Result applyItem(ItemStack item){
        if(CruxItem.isEmpty(item)) return Result.DEFAULT;

        var result = itemConsumer.applyItem(item);
        if(result.shouldStopIterating()) return result;

        if(CruxItem.isEmpty(item)) return Result.DEFAULT;

        return applyItemsIfStorage(item, item.getItemMeta());
    }

    public Result executeIfIterating(Result result, Supplier<Result> run){
        if(result.shouldStopIterating()) return result;
        return run.get();
    }

    public Result applyAllItems(HumanEntity p){
        return executeIfIterating(
            applyItems(p.getInventory()),
            () -> executeIfIterating(
                applyItem(p.getItemOnCursor()),
                () -> {
                    ItemPackHolder itemPackHolder = ItemPackHolder.itemPackHolder(p);
                    if(itemPackHolder != null) return applyItems(itemPackHolder);
                    return Result.DEFAULT;
                }
            )
        );

        /*applyItems(p.getInventory());
        applyItem(p.getItemOnCursor());
        ItemPackHolder itemPackHolder = ItemPackHolder.itemPackHolder(p);
        if(itemPackHolder != null) applyItems(itemPackHolder);
        return this;*/
    }

    public Result applyItemsIfStorage(ItemStack item, ItemMeta meta){
        if(meta instanceof BundleMeta m && m.hasItems()){
            var list = new ArrayList<>(m.getItems());
            var result = applyItems(list, true);
            m.setItems(list);
            item.setItemMeta(m);

            if(result.shouldStopIterating()) return result;
        }
        if(meta instanceof BlockStateMeta m){
            if(m.getBlockState() instanceof InventoryHolder h){
                var result = applyItems(h.getInventory());
                m.setBlockState((BlockState) h);
                item.setItemMeta(m);
                if(result.shouldStopIterating()) return result;
            }
        }
        return Result.DEFAULT;
    }

    public Result applyItems(Collection<ItemStack> list, boolean removeIfEmpty){
        if(list.isEmpty()) return Result.DEFAULT;

        if(removeIfEmpty){
            var iterator = list.iterator();
            while(iterator.hasNext()){
                var item = iterator.next();
                var result = applyItem(item);

                if(CruxItem.isEmpty(item)){
                    iterator.remove();
                }

                if(result.shouldStopIterating()){
                    return Result.STOP_ITERATING;
                }
            }

            return Result.DEFAULT;
        }

        for (ItemStack item : list) {
            var result = applyItem(item);
            if(result.shouldStopIterating()){
                return result;
            }
        }
        return Result.DEFAULT;
    }

    /*public Result applyItems(Collection<ItemStack> list, boolean removeIfEmpty){
        if(!removeIfEmpty) return applyItems(list);

        for (ItemStack item : list) {
            var result = applyItem(item);
            if(result.shouldStopIterating()){
                return result;
            }
        }
        return Result.DEFAULT;
    }*/

    public Result applyItems(Inventory inv){
        for (ItemStack item : inv) {
            var result = applyItem(item);
            if(result.shouldStopIterating()){
                return result;
            }
        }
        return Result.DEFAULT;
    }

    public Result applyItems(ItemPackHolder holder){
        return applyItems(holder.getPack());
    }

    public Result applyItems(Pack pack){
        var iterator = pack.get().values().iterator();

        while(iterator.hasNext()){
            var value = iterator.next();
            var result = applyItems(value);
            if(value.isEmpty()){
                iterator.remove();
            }
            if(result.shouldStopIterating()) break;
        }
        return Result.DEFAULT;
    }

    public Result applyItems(Pack.Page packPage){
        var iterator = packPage.get().values().iterator();
        while(iterator.hasNext()){
            ItemStack value = iterator.next();
            var result = applyItem(value);
            if(CruxItem.isEmpty(value)){
                iterator.remove();
            }
            if(result.shouldStopIterating()){
                return result;
            }
        }
        return Result.DEFAULT;
    }

    public interface ItemApplier<T>{
        @NotNull Result applyItem(T item);
    }

    public interface Result{
        Result DEFAULT = new Result() {
            @Override
            public boolean shouldStopIterating() {
                return false;
            }
        };
        Result STOP_ITERATING = new Result() {
            @Override
            public boolean shouldStopIterating() {
                return true;
            }
        };
        boolean shouldStopIterating();
    }
}
