package killercreepr.usurvive;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.event.RegistryEvents;
import killercreepr.cruxenchants.core.registries.CruxEnchantRegistries;
import killercreepr.usurvive.enchant.USurviveEnchantRegisterer;

public class USurviveBootstrapper implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
            new USurviveEnchantRegisterer(event.registry(), CruxEnchantRegistries.ENCHANT_WRAPPER, event).register();
        }));
    }
}
