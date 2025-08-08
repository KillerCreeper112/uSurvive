package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.usurvive.core.enchant.USurviveEnchant;
import killercreepr.usurvive.core.entity.tickable.ActiveEmotiveEnchantTickable;
import killercreepr.usurvive.core.entity.tickable.USurviveTickables;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmotiveMobHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("enchant/emotive/mob");
    public EmotiveMobHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
        this.tickable = new ActiveEmotiveEnchantTickable(parent.value(), USurviveTickables.EMOTIVE, CruxSlot.HEAD);
    }
    public EmotiveMobHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    protected final ActiveEmotiveEnchantTickable tickable;

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Entity e) {
        if(super.shouldRemoveFromMemory(e)) return true;
        ItemStack item = tickable.getLiving().getEquipment().getHelmet();
        if(CruxItem.isEmpty(item) || item.getEnchantmentLevel(USurviveEnchant.EMOTIVE) < 1){
            return true;
        }
        return false;
    }

    @Override
    public void adding() {
        super.adding();
        tickable.started();
    }

    @Override
    protected void removingFromMemory(@Nullable Entity e) {
        super.removingFromMemory(e);
        tickable.stopped();
    }

    @Override
    public void tick(@NotNull Entity entity) {
        tickable.tick();
    }
}
