package killercreepr.usurvive.core.listener.armortrim;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.component.TypedDataComponent;
import killercreepr.crux.api.item.CruxItemType;
import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxKey;
import killercreepr.cruxenchantsoverhaul.CruxEnchantsOverhaul;
import killercreepr.cruxenchantsoverhaul.component.EnchantComponents;
import killercreepr.cruxitems.api.item.CruxedItem;
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.cruxitems.core.item.plugin.CfgPluginItem;
import killercreepr.cruxitems.core.registries.CruxItemRegistries;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.component.impl.BaseItemUpgradedFrom;
import net.kyori.adventure.key.Key;
import org.bukkit.Color;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.Collection;

public class MoulditeDiamondResult implements ArmorSmithResult{
    @Override
    public ItemStack createResult(ItemStack upgrade, ItemStack tool, ItemStack material) {
        ItemStack result = tool.clone();
        CruxedItem cruxed = CruxedItem.cruxed(result);
        Key toolKey = Crux.handlers().item().getType(result);

        Key moulditeKey = getMoulditeKey(toolKey);

        PluginItem item = getMoulditeRawItem(Crux.handlers().item().getType(result));
        if(!(item instanceof CfgPluginItem cfg)){
            Crux.logError(item.key() + " is not a CfgPluginItem!");
            return null;
        }

        cruxed.setPluginItem(moulditeKey);

        int damage = result.getDataOrDefault(DataComponentTypes.DAMAGE, 0);
        var previousName = result.getData(DataComponentTypes.CUSTOM_NAME);
        cruxed.editMeta(meta ->{
            var modifiers = meta.getAttributeModifiers();
            if(modifiers == null) return;
            modifiers.forEach((attribute, mod) ->{
                if(mod.key().equals(toolKey)){
                    meta.removeAttributeModifier(attribute, mod);
                    meta.addAttributeModifier(attribute, new AttributeModifier(
                        CruxKey.key(moulditeKey), mod.getAmount(), mod.getOperation(), mod.getSlotGroup()
                    ));
                }
            });
        });

        Collection<TypedDataComponent<?>> typedList = new ArrayList<>();
        cruxed.forEach(typedList::add);


        cfg.dynamicItem().applyComponents(cruxed, TextParserContext.empty());
        cruxed.editMeta(Damageable.class, d -> d.setDamage(damage));

        if(previousName != null) result.setData(DataComponentTypes.CUSTOM_NAME, previousName);

        ItemStack builtItem = item.buildItem();
        var equip = builtItem.getData(DataComponentTypes.EQUIPPABLE);
        if(equip != null) result.setData(DataComponentTypes.EQUIPPABLE, equip);

        typedList.forEach(cruxed::set);
        /*builtItem.getDataTypes().forEach(data ->{
            try{
                if(data instanceof DataComponentType.Valued valued){
                    result.setData(valued, builtItem.getData(valued));
                }else if(data instanceof DataComponentType.NonValued v){
                    result.setData(v);
                }
            }catch (Exception ignored){}
        });*/
        cruxed.set(USurviveComponents.BASE_ITEM_UPGRADED_FROM, new BaseItemUpgradedFrom("diamond", Color.fromRGB(0x6EECD2)));
        cruxed.set(EnchantComponents.MAGIC_CAPACITY, CruxEnchantsOverhaul.inst().getMagicCapacityHandler().getMagicCapacity(tool));

        CruxItemType toolType = Crux.handlers().item().getItemType(tool);
        if(toolType != null){
            toolType.forEachDefaultData(typed ->{
                if(cruxed.has(typed.getType())) return;
                cruxed.set(typed);
            });
        }

        Crux.handlers().item().update(result);

        return result;
    }

    public PluginItem getMouldite(Key key){
        return CruxItemRegistries.ITEMS.get(getMoulditeKey(key));
    }
    public PluginItem getMoulditeRawItem(Key key){
        return CruxItemRegistries.ITEMS.get(getMoulditeKeyRaw(key));
    }

    public Key getMoulditeKeyRaw(Key key){
        String[] args = key.value().split("_", 2);
        return Crux.key("mouldite_diamond_" + args[1]);
    }

    public Key getMoulditeKey(Key key){
        String[] args = key.value().split("_", 2);
        return Crux.key("mouldite_" + args[1]);
    }
}
