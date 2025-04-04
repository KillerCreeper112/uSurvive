package killercreepr.usurvive.api.event;

import killercreepr.crux.api.item.CruxItem;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class PostItemInfusePotionsEvent extends PostItemStackInfusePotionsEvent{
    protected final Entity itemHolder;
    public PostItemInfusePotionsEvent(ItemStack item, List<PotionEffect> potions, CruxItem result, Entity itemHolder) {
        super(item, potions, result);
        this.itemHolder = itemHolder;
    }

    public Entity getItemHolder() {
        return itemHolder;
    }
}
