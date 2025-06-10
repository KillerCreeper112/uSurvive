package killercreepr.usurvive.core.listener.armortrim;

import org.bukkit.inventory.ItemStack;

public interface ArmorSmithResult{
        ItemStack createResult(ItemStack upgrade, ItemStack tool, ItemStack material);
}