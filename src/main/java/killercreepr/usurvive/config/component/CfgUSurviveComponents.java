package killercreepr.usurvive.config.component;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.component.TypedDataComponent;
import killercreepr.crux.registry.MappedRegistry;
import killercreepr.cruxconfig.config.bukkit.handler.impl.component.FileDataComponentType;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.usurvive.component.USurviveComponents;
import killercreepr.usurvive.component.impl.OrbitConduitComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CfgUSurviveComponents {
    public static void register(@NotNull MappedRegistry<String, FileDataComponentType<?>> registry){
        registry.register("orbit_conduit", new FileDataComponentType<OrbitConduitComponent>() {
            @Override
            public @Nullable TypedDataComponent<OrbitConduitComponent> deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e) {
                FileRegistry reg = ctx.getRegistry();
                List<String> interactCommands = reg.deserializeFromFile(
                    new TypeToken<List<String>>(){}.getType(), e.get("interact_commands"));
                if(interactCommands == null) return null;
                return TypedDataComponent.create(
                    USurviveComponents.DEPOSIT_CONDUIT, new OrbitConduitComponent(
                        interactCommands
                    )
                );
            }
        });
    }
}
