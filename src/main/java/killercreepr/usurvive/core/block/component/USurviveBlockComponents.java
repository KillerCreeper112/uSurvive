package killercreepr.usurvive.core.block.component;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.component.DataComponentType;
import killercreepr.crux.api.component.TypedDataComponent;
import killercreepr.crux.api.data.ParticleBuilderSupplier;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.cruxblocks.core.block.component.standard.EntitySpawnerComponent;
import killercreepr.cruxblocks.core.config.handler.component.FileEntitySpawnerComponent;
import killercreepr.cruxconfig.config.bukkit.handler.impl.component.FileDataComponentType;
import killercreepr.cruxconfig.config.bukkit.registry.FileDataComponentRegistry;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.usurvive.core.block.GlintingOreBlockComponent;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.component.impl.SmokeParticleBlockComponent;
import killercreepr.usurvive.core.component.impl.SoulParticleBlockComponent;
import killercreepr.usurvive.core.component.impl.WatcherPillarComponent;
import killercreepr.usurvive.core.component.impl.WatcherPillarOpenComponent;
import net.kyori.adventure.key.Key;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class USurviveBlockComponents {
    public static void register(@NotNull FileDataComponentRegistry registry){
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
        registry.register("glinting_ore_block", new FileDataComponentType<GlintingOreBlockComponent>() {
            @Override
            public @Nullable TypedDataComponent<GlintingOreBlockComponent> deserializeFromFile(@NotNull FileContext<?> fileContext, @NotNull FileObject fileObject) {
                return TypedDataComponent.create(USurviveComponents.GLINTING_ORE_BLOCK, new GlintingOreBlockComponent());
            }
        });
        registry.register("watcher_pillar", new FileDataComponentType<WatcherPillarComponent>() {
            @Override
            public @Nullable TypedDataComponent<WatcherPillarComponent> deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject o) {
                FileRegistry r = ctx.getRegistry();
                return TypedDataComponent.create(USurviveComponents.WATCHER_PILLAR_BLOCK, new WatcherPillarComponent(
                    r.deserializeFromFile(Key.class, o.get("other_block_group")),
                    r.deserializeFromFile(Boolean.class, o.get("open")),
                    r.deserializeFromFile(Float.class, o.get("night_time_open_chance")),
                    r.deserializeFromFile(CreateSound.class, o.get("change_to_other_sound")),
                    r.deserializeFromFile(NumberProvider.class, o.get("max_spawns_per_night"))
                ));
            }
        });
        registry.register("watcher_pillar_open", new FileDataComponentType<WatcherPillarComponent>() {
            @Override
            public @Nullable TypedDataComponent<WatcherPillarComponent> deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject o) {
                FileRegistry r = ctx.getRegistry();
                return TypedDataComponent.create(USurviveComponents.WATCHER_PILLAR_BLOCK, new WatcherPillarOpenComponent(
                    r.deserializeFromFile(Key.class, o.get("other_block_group")),
                    r.deserializeFromFile(Boolean.class, o.get("open")),
                    r.deserializeFromFile(Float.class, o.get("night_time_open_chance")),
                    r.deserializeFromFile(CreateSound.class, o.get("change_to_other_sound")),
                    r.deserializeFromFile(NumberProvider.class, o.get("max_spawns_per_night")),

                    r.deserializeFromFile(new TypeToken<Collection<PotionEffect>>(){}.getType(), o.get("nearby_potion_effects")),
                    r.deserializeFromFile(Float.class, o.get("potion_effect_range")),
                    r.deserializeFromFile(ParticleBuilderSupplier.class, o.get("particle")),
                    new FileEntitySpawnerComponent<EntitySpawnerComponent>(){
                        @Override
                        public EntitySpawnerComponent createSpawner(@NotNull FileContext<?> fileContext, @NotNull FileObject fileObject) {
                            return createGenericSpawner(fileContext, fileObject);
                        }

                        @Override
                        public DataComponentType<EntitySpawnerComponent> componentType() {
                            return null;
                        }
                    }.createSpawner(ctx, o.get("entity_spawner").getAsFileObject())
                ));
            }
        });
    }
}
