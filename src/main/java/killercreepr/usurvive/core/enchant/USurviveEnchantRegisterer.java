package killercreepr.usurvive.core.enchant;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import io.papermc.paper.registry.event.WritableRegistry;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import io.papermc.paper.tag.PreFlattenTagRegistrar;
import io.papermc.paper.tag.TagEntry;
import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.core.Crux;
import killercreepr.cruxenchants.api.enchant.ApplicableItemGroup;
import killercreepr.cruxenchants.api.enchant.ApplicableItemType;
import killercreepr.cruxenchants.api.enchant.CruxEnchant;
import killercreepr.cruxenchants.core.util.EnchantRegistererUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class USurviveEnchantRegisterer extends EnchantRegistererUtil {
    public USurviveEnchantRegisterer(WritableRegistry<Enchantment, EnchantmentRegistryEntry.Builder> registry,
                                     @Nullable KeyedRegistry<CruxEnchant> wrapperRegistry,
                                     RegistryFreezeEvent<Enchantment, EnchantmentRegistryEntry.Builder> event) {
        super(registry, wrapperRegistry, event);
    }
    public static final TagKey<ItemType> SHARP_WEAPON_PICKAXE = TagKey.create(RegistryKey.ITEM, Key.key("crux", "sharp_weapon_pickaxe"));

    public static void register(LifecycleEventManager<BootstrapContext> manager){
        manager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM), event -> {
            final PreFlattenTagRegistrar<ItemType> registrar = event.registrar();
            registrar.setTag(SHARP_WEAPON_PICKAXE, Set.of(
                TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_SHARP_WEAPON, true),
                TagEntry.tagEntry(ItemTypeTagKeys.PICKAXES, true)
            ));
        });

        manager.registerEventHandler(LifecycleEvents.TAGS.postFlatten(RegistryKey.ENCHANTMENT), event -> {
            final PostFlattenTagRegistrar<Enchantment> registrar = event.registrar();
            registrar.addToTag(
                EnchantmentTagKeys.IN_ENCHANTING_TABLE,
                Set.of(
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("trample")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("joy_whirl")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("burning_thorns")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("backstab")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("withstand")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("second_wind")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("boomshot")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("velocity")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("frostbite")),
                    TypedKey.create(RegistryKey.ENCHANTMENT, Crux.key("insight"))
                )
            );
        });
    }

    @Override
    public void register() {

        register(
            "trample", b -> b.description(Component.text("Trample"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR))
                .anvilCost(1)
                .maxLevel(3)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.FEET)
                .exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("feather_falling")))),

            crux -> crux.description("Take less fall damage when landing on an entity and damage them in the process")
                .applicableItemGroup(ApplicableItemGroup.itemGroup(ApplicableItemType.BOOTS))
        );
        register(
            "joy_whirl", b -> b.description(Component.text("Joy Whirl"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_SWORD))
                .anvilCost(1)
                .maxLevel(3)
                .weight(3)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),

            crux -> crux.description("Launches hit targets in the air and spins them around")
                .applicableItemGroup(ApplicableItemGroup.itemGroup(ApplicableItemType.SWORD))
                //.exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("feather_falling"))))
        );
        register(
            "burning_thorns", b -> b.description(Component.text("Burning Thorns"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_ARMOR))
                .anvilCost(1)
                .maxLevel(3)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.ARMOR)
                .exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("thorns")))),

            crux -> crux.description("Chance to damage and set entities who hit the wearer on fire")
                .applicableItemGroup(ApplicableItemGroup.ARMOR)
        );
        register(
            "backstab", b -> b.description(Component.text("Backstab"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                .anvilCost(1)
                .maxLevel(3)
                .weight(4)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),

            crux -> crux.description("Deal more damage when striking a target from behind")
                .applicableItemGroup(ApplicableItemGroup.WEAPONS)
        );
        register(
            "withstand", b -> b.description(Component.text("Withstand"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR))
                .anvilCost(1)
                .maxLevel(4)
                .weight(4)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.CHEST),

            crux -> crux.description("Briefly gain Resistance after taking a big hit")
                .applicableItemGroup(ApplicableItemGroup.itemGroup(ApplicableItemType.CHESTPLATE))
        );
        register(
            "second_wind", b -> b.description(Component.text("Second Wind"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR))
                .anvilCost(1)
                .maxLevel(3)
                .weight(3)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.FEET),

            crux -> crux.description("Sprint faster when on low health")
                .applicableItemGroup(ApplicableItemGroup.itemGroup(ApplicableItemType.BOOTS))
        );
        register(
            "boomshot", b -> b.description(Component.text("Boomshot"))
                .supportedItems(RegistrySet.keySet(RegistryKey.ITEM,
                    TypedKey.create(RegistryKey.ITEM, Key.key("bow")),
                    TypedKey.create(RegistryKey.ITEM, Key.key("crossbow"))
                ))
                .anvilCost(1)
                .maxLevel(5)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.HAND),

            crux -> crux.description("Chance to launch an arrow that explodes upon impact")
                .applicableItemGroup(ApplicableItemGroup.BOWS_N_CROSSBOWS)
        );
        register(
            "velocity", b -> b.description(Component.text("Velocity"))
                .supportedItems(RegistrySet.keySet(RegistryKey.ITEM,
                    TypedKey.create(RegistryKey.ITEM, Key.key("bow")),
                    TypedKey.create(RegistryKey.ITEM, Key.key("crossbow"))
                ))
                .anvilCost(1)
                .maxLevel(2)
                .weight(1)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.HAND),

            crux -> crux.description("Increases projectile speed")
                .applicableItemGroup(ApplicableItemGroup.BOWS_N_CROSSBOWS)
        );
        register(
            "frostbite", b -> b.description(Component.text("Frostbite"))
                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                .anvilCost(1)
                .maxLevel(2)
                .weight(2)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.HAND)
                .exclusiveWith(RegistrySet.keySet(RegistryKey.ENCHANTMENT, TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("fire_aspect")))),

            crux -> crux.description("Chance to freeze and slow down the target upon hit")
                .applicableItemGroup(ApplicableItemGroup.WEAPONS)
        );
        register(
            "insight", b -> b.description(Component.text("Insight"))
                .supportedItems(event.getOrCreateTag(SHARP_WEAPON_PICKAXE))
                .anvilCost(1)
                .maxLevel(3)
                .weight(2)
                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                .activeSlots(EquipmentSlotGroup.MAINHAND),

            crux -> crux.description("Chance to receive more exp from mining or killing mobs")
                .applicableItemGroup(ApplicableItemGroup.builder()
                    .add(ApplicableItemType.PICKAXE)
                    .add(ApplicableItemGroup.SHARP_WEAPONS)
                    .build())
        );
    }
}
