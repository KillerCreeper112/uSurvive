package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CleansingEnchantHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("enchantment/cleansing");

    public CleansingEnchantHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }
    public CleansingEnchantHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    protected boolean remove = false;
    @Override
    public boolean shouldRemoveFromMemory(@Nullable Entity e) {
        return super.shouldRemoveFromMemory(e) || remove;
    }

    @Override
    public void tick(@NotNull Entity entity) {

    }
}
