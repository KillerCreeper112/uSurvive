package killercreepr.usurvive.data.entity;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrampleHolder extends PlayerTickedDataHolder {
    public static final Key KEY = Crux.key("usurvive/enchant/trample");

    protected int enchantLevel;
    public TrampleHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
    }

    public TrampleHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    protected int trampleAmount;
    @Override
    public boolean shouldRemoveFromMemory(@Nullable Player e) {
        return super.shouldRemoveFromMemory(e) || e == null || e.isOnGround();
    }

    public boolean canTrample(){
        return trampleAmount < getMaxTrampleAmount();
    }

    public int getMaxTrampleAmount(){
        return enchantLevel + 1;
    }

    public int getTrampleAmount() {
        return trampleAmount;
    }

    public void setTrampleAmount(int trampleAmount) {
        this.trampleAmount = trampleAmount;
    }

    public void setEnchantLevel(int enchantLevel) {
        this.enchantLevel = enchantLevel;
    }
}
