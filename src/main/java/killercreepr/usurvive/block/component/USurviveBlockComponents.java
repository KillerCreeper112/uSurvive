package killercreepr.usurvive.block.component;

import killercreepr.crux.api.component.TypedDataComponent;
import killercreepr.crux.api.registry.MappedRegistry;
import killercreepr.cruxconfig.config.bukkit.handler.impl.component.FileDataComponentType;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.usurvive.component.USurviveComponents;
import killercreepr.usurvive.component.impl.SmokeParticleBlockComponent;
import killercreepr.usurvive.component.impl.SoulParticleBlockComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class USurviveBlockComponents {
    public static void register(@NotNull MappedRegistry<String, FileDataComponentType<?>> registry){
        registry.register("soul_particle_block", new FileDataComponentType<SoulParticleBlockComponent>() {
            @Override
            public @Nullable TypedDataComponent<SoulParticleBlockComponent> deserializeFromFile(@NotNull FileContext<?> fileContext, @NotNull FileObject fileObject) {
                return TypedDataComponent.create(USurviveComponents.SOUL_PARTICLE_BLOCK, new SoulParticleBlockComponent());
            }
        });
        registry.register("smoke_particle_block", new FileDataComponentType<SmokeParticleBlockComponent>() {
            @Override
            public @Nullable TypedDataComponent<SmokeParticleBlockComponent> deserializeFromFile(@NotNull FileContext<?> fileContext, @NotNull FileObject fileObject) {
                return TypedDataComponent.create(USurviveComponents.SMOKE_PARTICLE_BLOCK, new SmokeParticleBlockComponent());
            }
        });
    }
}
