package killercreepr.usurvive.config.handler;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.item.StoredItem;
import killercreepr.cruxconfig.config.common.handler.SimpleFileConverter;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class StoredItemsConverter extends SimpleFileConverter<Collection<ItemStack>, Collection<StoredItem>> {

    @Override
    public Type toType() {
        return new TypeToken<Collection<StoredItem>>(){}.getType();
    }

    @Override
    public Collection<ItemStack> convertTo(Collection<StoredItem> storedItems) {
        Collection<ItemStack> list = new ArrayList<>();
        storedItems.forEach(e -> list.add(e.item()));
        return list;
    }

    @Override
    public Collection<StoredItem> convertFrom(Collection<ItemStack> itemStacks) {
        Collection<StoredItem> list = new ArrayList<>();
        itemStacks.forEach(e -> list.add(new StoredItem(e)));
        return list;
    }
}
