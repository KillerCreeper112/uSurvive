package killercreepr.usurvive.core.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.predicate.ItemPredicate;
import killercreepr.crux.core.Crux;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class ArmorTrimListener implements Listener {
    protected final ItemPredicate trim = ItemPredicate.fromType(Crux.key("abyssal_relic"));
    @EventHandler(ignoreCancelled = true)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        SmithingInventory inv = event.getInventory();
        ItemStack template = inv.getInputTemplate();
        if(CruxItem.isEmpty(template) || !trim.test(template)) return;
        ItemStack armor = inv.getInputEquipment();
        if(CruxItem.isEmpty(armor)) return;
        ItemStack gem = inv.getInputMineral();
        if(CruxItem.isEmpty(gem)) return;

        TrimMaterial material = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL)
            .get(Key.key(gem.getType().key().value().split("_")[0]));
        if(material==null) return;

        TrimPattern trim = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN)
            .get(Crux.key("abyssal_relic"));

        ItemStack result = armor.clone();
        result.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(
            new ArmorTrim(material, trim)
        ));
        event.setResult(result);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getView().getTopInventory() instanceof SmithingInventory inv)) return;
        ItemStack cursor = event.getCursor();
        ItemStack item = event.getCurrentItem();

        if(event.getClickedInventory() instanceof SmithingInventory clickedInv){
            if(CruxItem.isEmpty(cursor)) return;
            if(!trim.test(cursor)) return;

            int slot = event.getSlot();
            if(slot != 0) return;
            if(!CruxItem.isEmpty(item)) return;
            event.setCancelled(true);
            ItemStack putIn = cursor.clone();
            putIn.setAmount(1);
            cursor.setAmount(cursor.getAmount()-1);
            clickedInv.setItem(slot, putIn);
            return;
        }

        if(!event.isShiftClick()) return;
        if(CruxItem.isEmpty(item)) return;
        if(!trim.test(item)) return;
        ItemStack invInput = inv.getInputTemplate();
        if(!CruxItem.isEmpty(invInput)) return;
        event.setCancelled(true);
        ItemStack putIn = item.clone();
        putIn.setAmount(1);
        item.setAmount(item.getAmount()-1);
        inv.setInputTemplate(putIn);
    }

}
