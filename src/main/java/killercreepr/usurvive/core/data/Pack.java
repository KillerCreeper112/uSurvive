package killercreepr.usurvive.core.data;

import killercreepr.crux.api.item.CruxItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Pack {
    protected final Map<Integer, Page> stored = new HashMap<>();
    public @NotNull Map<Integer, Page> get() {
        return stored;
    }
    public Pack set(@NotNull Map<Integer, Page> stored){
        this.stored.clear();
        this.stored.putAll(stored);
        return this;
    }

    public boolean isEmpty(){
        return stored.isEmpty();
    }

    public @Nullable Page get(int index){
        return stored.getOrDefault(index, null);
    }

    public Pack setPage(int index, @Nullable Page page){
        if(page == null || page.isEmpty()) stored.remove(index);
        else stored.put(index, page);
        return this;
    }

    public static class Page{
        protected final Map<Integer, ItemStack> stored = new HashMap<>();

        public boolean isEmpty(){
            return stored.isEmpty();
        }

        public Page set(int index, ItemStack item){
            if(CruxItem.isEmpty(item)) stored.remove(index);
            else stored.put(index, item);
            return this;
        }

        public @NotNull Map<Integer, ItemStack> get() {
            return stored;
        }
        public Page set(@NotNull Map<Integer, ItemStack> stored){
            this.stored.clear();
            this.stored.putAll(stored);
            return this;
        }
    }
}
