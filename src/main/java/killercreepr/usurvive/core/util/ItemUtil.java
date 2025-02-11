package killercreepr.usurvive.core.util;

import com.destroystokyo.paper.MaterialTags;
import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {
    public static CruxSlotGroup getDefaultSlotGroup(ItemStack item){
        if(MaterialTags.HELMETS.isTagged(item)) return CruxSlotGroup.HEAD;
        if(MaterialTags.CHESTPLATES.isTagged(item)) return CruxSlotGroup.CHEST;
        if(MaterialTags.LEGGINGS.isTagged(item)) return CruxSlotGroup.LEGS;
        if(MaterialTags.BOOTS.isTagged(item)) return CruxSlotGroup.FEET;
        return CruxSlotGroup.MAIN_HAND;
    }
}
