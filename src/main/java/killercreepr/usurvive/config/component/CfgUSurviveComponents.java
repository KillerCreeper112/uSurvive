package killercreepr.usurvive.config.component;

import killercreepr.crux.component.TypedDataComponent;
import killercreepr.crux.registry.MappedRegistry;
import killercreepr.cruxconfig.config.bukkit.handler.impl.component.FileDataComponentType;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.usurvive.component.USurviveComponents;
import killercreepr.usurvive.component.impl.OrbitConduitComponent;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CfgUSurviveComponents {
    public static void register(@NotNull MappedRegistry<String, FileDataComponentType<?>> registry){
        registry.register("orbit_conduit", new FileDataComponentType<OrbitConduitComponent>() {
            @Override
            public @Nullable TypedDataComponent<OrbitConduitComponent> deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e) {
                FileRegistry reg = ctx.getRegistry();
                Key currency = reg.deserializeFromFile(Key.class, e.get("currency"));
                if(currency == null) return null;
                return TypedDataComponent.create(
                    USurviveComponents.DEPOSIT_CONDUIT, new OrbitConduitComponent(
                        currency
                    )
                );
            }
        });
    }
}
