package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityDataHolder;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.usurvive.core.data.Pack;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemPackHolder extends EntityDataHolder {
    public static final Key KEY = Crux.key("item_pack");
    public ItemPackHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }
    public ItemPackHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    protected final Pack pack = new Pack();

    public ItemPackHolder setPack(@Nullable Pack map){
        pack.get().clear();
        if(map == null) return this;
        pack.get().putAll(map.get());
        return this;
    }

    public Pack getPack() {
        return pack;
    }
}
