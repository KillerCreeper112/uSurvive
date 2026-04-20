package killercreepr.usurvive.core.component;

import killercreepr.crux.api.component.DataComponentType;
import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.parser.type.ComponentInputParsers;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxattributes.core.component.CruxAttributeCompParsers;
import killercreepr.cruxteleport.api.component.TeleporterComponent;
import killercreepr.usurvive.api.component.InfusedPotions;
import killercreepr.usurvive.api.component.PotionInfusable;
import killercreepr.usurvive.api.component.WaterProjectile;
import killercreepr.usurvive.api.enchant.component.CruxEnchantBlockDrops;
import killercreepr.usurvive.api.enchant.component.CruxEnchantInteractable;
import killercreepr.usurvive.api.enchant.component.CruxEnchantPhysicalInteractable;
import killercreepr.usurvive.api.mechanics.grapple.Grapple;
import killercreepr.usurvive.api.mechanics.whip.Whip;
import killercreepr.usurvive.core.block.GlintingOreBlockComponent;
import killercreepr.usurvive.core.component.impl.*;
import killercreepr.usurvive.core.component.teleport.TeleportCheckInStructure;
import killercreepr.usurvive.core.component.teleport.TeleportSetLastTP;
import killercreepr.usurvive.core.mechanics.*;
import net.kyori.adventure.key.Key;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class USurviveEnchantComponents {
    public static void register(){}
    public static final DataComponentType<CruxEnchantInteractable> INTERACTABLE = register("crux/enchant/interactable", builder -> builder);
    public static final DataComponentType<CruxEnchantBlockDrops> BLOCK_DROPS = register("crux/enchant/block_drops", builder -> builder);
    public static final DataComponentType<CruxEnchantPhysicalInteractable> PHYSICAL_INTERACTABLE = register("crux/enchant/physical_interactable", builder -> builder);

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}