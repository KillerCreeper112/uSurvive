package killercreepr.usurvive.core;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import killercreepr.crux.core.Crux;
import killercreepr.cruxenchants.core.registries.CruxEnchantRegistries;
import killercreepr.usurvive.core.enchant.USurviveEnchantRegisterer;
import org.bukkit.damage.DamageEffect;
import org.bukkit.damage.DamageScaling;
import org.bukkit.damage.DamageType;

import java.util.List;

public class USurviveBootstrapper implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        USurviveEnchantRegisterer.register(context.getLifecycleManager());
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
            new USurviveEnchantRegisterer(event.registry(), CruxEnchantRegistries.CRUX_ENCHANT, event).register();
        }));

        context.getLifecycleManager().registerEventHandler(RegistryEvents.DAMAGE_TYPE.freeze().newHandler(event -> {

            event.registry().register(
                RegistryKey.DAMAGE_TYPE.typedKey(Crux.key("thornlash")),
                builder -> builder.exhaustion(0.15f)
                    .messageId("thornlash")
                    .damageScaling(DamageScaling.ALWAYS)
                    .damageEffect(DamageEffect.POKING)
            );

            event.registry().register(
                RegistryKey.DAMAGE_TYPE.typedKey(Crux.key("entity_attack_bypass_shield")),
                builder -> builder.exhaustion(0.25f)
                    .messageId("entity_attack_bypass_shield")
                    .damageScaling(DamageScaling.ALWAYS)
            );
        }));



        context.getLifecycleManager().registerEventHandler(LifecycleEvents.TAGS.postFlatten(RegistryKey.DAMAGE_TYPE), event -> {
            final PostFlattenTagRegistrar<DamageType> registrar = event.registrar();

            registrar.addToTag(
                DamageTypeTagKeys.NO_KNOCKBACK,
                List.of(
                    TypedKey.create(RegistryKey.DAMAGE_TYPE, Crux.key("thornlash"))
                )
            );

            registrar.addToTag(
                DamageTypeTagKeys.BYPASSES_SHIELD,
                List.of(
                    TypedKey.create(RegistryKey.DAMAGE_TYPE, Crux.key("entity_attack_bypass_shield"))
                )
            );
        });
    }
}
