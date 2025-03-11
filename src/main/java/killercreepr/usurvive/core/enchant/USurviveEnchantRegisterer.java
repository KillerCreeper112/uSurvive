package killercreepr.usurvive.core.enchant;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.event.WritableRegistry;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.cruxenchants.api.enchant.CruxEnchantWrapper;
import killercreepr.cruxenchants.core.util.EnchantRegistererUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.Nullable;

public class USurviveEnchantRegisterer extends EnchantRegistererUtil {
    public USurviveEnchantRegisterer(WritableRegistry<Enchantment, EnchantmentRegistryEntry.Builder> registry,
                                     @Nullable KeyedRegistry<CruxEnchantWrapper> wrapperRegistry,
                                     RegistryFreezeEvent<Enchantment, EnchantmentRegistryEntry.Builder> event) {
        super(registry, wrapperRegistry, event);
    }

    @Override
    public void register() {
        register(
            "trample", b -> b.description(Component.text("Trample"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR))
                .anvilCost(1)
                .maxLevel(3)
                .weight(5)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.FEET)
                .exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("feather_falling"))))
        );
        register(
            "joy_whirl", b -> b.description(Component.text("Joy Whirl"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_SWORD))
                .anvilCost(1)
                .maxLevel(3)
                .weight(5)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND)
                //.exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("feather_falling"))))
        );
        register(
            "burning_thorns", b -> b.description(Component.text("Burning Thorns"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_ARMOR))
                .anvilCost(1)
                .maxLevel(1)
                .weight(5)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.ARMOR)
            //.exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("feather_falling"))))
        );
    }
}
