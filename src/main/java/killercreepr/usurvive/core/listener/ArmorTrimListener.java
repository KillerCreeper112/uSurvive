package killercreepr.usurvive.core.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.predicate.ItemPredicate;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.util.MapBuilder;
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.cruxitems.core.registries.CruxItemRegistries;
import killercreepr.usurvive.core.listener.armortrim.ArmorSmithResult;
import killercreepr.usurvive.core.listener.armortrim.MoulditeDiamondResult;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.Map;

public class ArmorTrimListener implements Listener {
    protected final ItemPredicate mouldite =  ItemPredicate.fromType(Crux.key("mouldite"));
    protected final ItemPredicate moulditeUpgrade =  ItemPredicate.fromType(Crux.key("mouldite_upgrade_smithing_template"));
    protected final ItemPredicate abyssalRelic =  ItemPredicate.fromType(Crux.key("abyssal_relic"));
    protected final ItemPredicate ironIngot =  ItemPredicate.fromType(Key.key("iron_ingot"));
    protected final ItemPredicate inputTrim = ItemPredicate.fromAnyOf(
        abyssalRelic,
        ironIngot,
        moulditeUpgrade
    );
    protected final ItemPredicate roseGoldIngot = ItemPredicate.fromType(Crux.key("rose_gold_ingot"));
    protected final ItemPredicate inputMaterial = ItemPredicate.fromAnyOf(
        roseGoldIngot,
        mouldite
    );
    protected final ItemPredicate inputEquipment = ItemPredicate.fromAnyOf(
        ItemPredicate.fromType(Key.key("golden_sword")),
        ItemPredicate.fromType(Key.key("golden_pickaxe")),
        ItemPredicate.fromType(Key.key("golden_axe")),
        ItemPredicate.fromType(Key.key("golden_shovel")),
        ItemPredicate.fromType(Key.key("golden_hoe"))
    );

    protected final Map<Key, Key> roseGolds = new MapBuilder<Key, Key>()
        .put(Key.key("golden_helmet"), Crux.key("rose_gold_helmet"))
        .put(Key.key("golden_chestplate"), Crux.key("rose_gold_chestplate"))
        .put(Key.key("golden_leggings"), Crux.key("rose_gold_leggings"))
        .put(Key.key("golden_boots"), Crux.key("rose_gold_boots"))

        .put(Key.key("golden_sword"), Crux.key("rose_gold_sword"))
        .put(Key.key("golden_pickaxe"), Crux.key("rose_gold_pickaxe"))
        .put(Key.key("golden_axe"), Crux.key("rose_gold_axe"))
        .put(Key.key("golden_shovel"), Crux.key("rose_gold_shovel"))
        .put(Key.key("golden_hoe"), Crux.key("rose_gold_hoe"))
        .buildUnmodifiable()
        ;

    protected final ArmorSmithResult moulditeDiamondResult = new MoulditeDiamondResult();

    protected final Map<Key, ArmorSmithResult> ARMOR_SMITH_RESULTS = new MapBuilder<Key, ArmorSmithResult>()
        .put(Key.key("diamond_helmet"), moulditeDiamondResult)
        .put(Key.key("diamond_chestplate"), moulditeDiamondResult)
        .put(Key.key("diamond_leggings"), moulditeDiamondResult)
        .put(Key.key("diamond_boots"), moulditeDiamondResult)

        .put(Key.key("diamond_sword"), moulditeDiamondResult)
        .put(Key.key("diamond_pickaxe"),moulditeDiamondResult)
        .put(Key.key("diamond_axe"), moulditeDiamondResult)
        .put(Key.key("diamond_shovel"), moulditeDiamondResult)
        .put(Key.key("diamond_hoe"), moulditeDiamondResult)
        .buildUnmodifiable()
        ;

    @EventHandler(ignoreCancelled = true)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        editMaterial(event);
        editTrim(event);
        roseGold(event);
        mouldite(event);
    }

    public void mouldite(PrepareSmithingEvent event){
        SmithingInventory inv = event.getInventory();
        ItemStack gem = inv.getInputMineral();
        if(CruxItem.isEmpty(gem)) return;
        ItemStack armor = inv.getInputEquipment();
        if(CruxItem.isEmpty(armor)) return;
        ItemStack template = inv.getInputTemplate();
        if(CruxItem.isEmpty(template)) return;

        if(!mouldite.test(gem)) return;
        if(!moulditeUpgrade.test(template)) return;

        var result = ARMOR_SMITH_RESULTS.get(Crux.handlers().item().getType(armor));
        if(result == null) return;
        event.setResult(result.createResult(template, armor, gem));
    }

    public void roseGold(PrepareSmithingEvent event){
        SmithingInventory inv = event.getInventory();
        ItemStack gem = inv.getInputMineral();
        if(CruxItem.isEmpty(gem)) return;
        ItemStack armor = inv.getInputEquipment();
        if(CruxItem.isEmpty(armor)) return;
        ItemStack template = inv.getInputTemplate();
        if(CruxItem.isEmpty(template)) return;

        if(!ironIngot.test(template)) return;
        if(!roseGoldIngot.test(gem)) return;

        Key inputKey = Crux.handlers().item().getType(armor);
        Key newItemKey = roseGolds.get(inputKey);
        if(newItemKey == null) return;

        PluginItem newItemPlugin = CruxItemRegistries.ITEMS.get(newItemKey);
        if(newItemPlugin == null) return;

        ItemStack newItem = newItemPlugin.buildItem();
        CruxItem inputCrux = CruxItem.wrap(armor);
        CruxItem newCrux = CruxItem.wrap(newItem);

        inputCrux.forEach(newCrux::set);

        armor.editMeta(meta ->{
            ItemMeta newMeta = newItem.getItemMeta();
            if(newMeta == null) return;
            meta.getEnchants().forEach((e, i) -> newMeta.addEnchant(e, i, true));
            newItem.setItemMeta(newMeta);
        });

        var trim = armor.getData(DataComponentTypes.TRIM);
        if(trim != null) newItem.setData(DataComponentTypes.TRIM, trim);

        armor.editMeta(Damageable.class, meta ->{
            newItem.editMeta(Damageable.class, newMeta ->{
                float percent = (float) meta.getDamage() / (float) CruxItem.getMaxDurability(armor);
                int difference = (int) (CruxItem.getMaxDurability(newItem) * percent);
                if(difference < 1) return;
                newMeta.setDamage(difference);
            });
        });

        Crux.handlers().item().update(newItem);
        event.setResult(newItem);
    }

    public void editMaterial(PrepareSmithingEvent event){
        SmithingInventory inv = event.getInventory();
        ItemStack gem = inv.getInputMineral();
        if(CruxItem.isEmpty(gem) || !roseGoldIngot.test(gem)) return;
        ItemStack armor = inv.getInputEquipment();
        if(CruxItem.isEmpty(armor)) return;
        ItemStack template = inv.getInputTemplate();
        if(CruxItem.isEmpty(template)) return;


        TrimMaterial material = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL)
            .get(Crux.key("rose_gold"));
        if(material==null) return;

        TrimPattern trim = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN)
            .get(Key.key(template.getType().key().value().split("_")[0]));
        if(trim == null) return;

        ItemStack result = armor.clone();
        result.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(
            new ArmorTrim(material, trim)
        ));
        event.setResult(result);
    }

    public void editTrim(PrepareSmithingEvent event){
        SmithingInventory inv = event.getInventory();
        ItemStack template = inv.getInputTemplate();
        if(CruxItem.isEmpty(template) || !abyssalRelic.test(template)) return;
        ItemStack armor = inv.getInputEquipment();
        if(CruxItem.isEmpty(armor)) return;
        ItemStack gem = inv.getInputMineral();
        if(CruxItem.isEmpty(gem)) return;

        TrimMaterial material = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL)
            .get(this.roseGoldIngot.test(gem) ? Crux.key("rose_gold") : Key.key(gem.getType().key().value().split("_")[0]));
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

            if(inputTrim.test(cursor)){
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
            if(inputMaterial.test(cursor)){
                int slot = event.getSlot();
                if(slot != 2) return;
                if(!CruxItem.isEmpty(item)) return;
                event.setCancelled(true);
                ItemStack putIn = cursor.clone();
                putIn.setAmount(1);
                cursor.setAmount(cursor.getAmount()-1);
                clickedInv.setItem(slot, putIn);
                return;
            }

            if(inputEquipment.test(cursor)){
                int slot = event.getSlot();
                if(slot != 1) return;
                if(!CruxItem.isEmpty(item)) return;
                event.setCancelled(true);
                ItemStack putIn = cursor.clone();
                putIn.setAmount(1);
                cursor.setAmount(cursor.getAmount()-1);
                clickedInv.setItem(slot, putIn);
            }
            return;
        }

        if(!event.isShiftClick()) return;
        if(CruxItem.isEmpty(item)) return;
        if(inputTrim.test(item)){
            ItemStack invInput = inv.getInputTemplate();
            if(!CruxItem.isEmpty(invInput)) return;
            event.setCancelled(true);
            ItemStack putIn = item.clone();
            putIn.setAmount(1);
            item.setAmount(item.getAmount()-1);
            inv.setInputTemplate(putIn);
            return;
        }
        if(inputMaterial.test(item)){
            ItemStack invInput = inv.getInputMineral();
            if(!CruxItem.isEmpty(invInput)) return;
            event.setCancelled(true);
            ItemStack putIn = item.clone();
            putIn.setAmount(1);
            item.setAmount(item.getAmount()-1);
            inv.setInputMineral(putIn);
            return;
        }

        if(inputEquipment.test(item)){
            ItemStack invInput = inv.getInputEquipment();
            if(!CruxItem.isEmpty(invInput)) return;
            event.setCancelled(true);
            ItemStack putIn = item.clone();
            putIn.setAmount(1);
            item.setAmount(item.getAmount()-1);
            inv.setInputEquipment(putIn);
        }
    }
}
