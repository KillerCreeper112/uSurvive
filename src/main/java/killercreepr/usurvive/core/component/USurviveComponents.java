package killercreepr.usurvive.core.component;

import killercreepr.crux.api.component.DataComponentType;
import killercreepr.crux.api.component.parser.ComponentTextInputParser;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxteleport.api.component.TeleporterComponent;
import killercreepr.usurvive.core.component.impl.OrbitConduitComponent;
import killercreepr.usurvive.core.component.impl.SmokeParticleBlockComponent;
import killercreepr.usurvive.core.component.impl.SoulParticleBlockComponent;

import java.util.function.UnaryOperator;

public class USurviveComponents {
    public static final DataComponentType<OrbitConduitComponent> DEPOSIT_CONDUIT = register("deposit_conduit", builder ->
        builder);
    public static final DataComponentType<SoulParticleBlockComponent> SOUL_PARTICLE_BLOCK = register("soul_particle_block", builder ->
        builder);
    public static final DataComponentType<SmokeParticleBlockComponent> SMOKE_PARTICLE_BLOCK = register("smoke_particle_block", builder ->
        builder);
    public static final DataComponentType<TeleporterComponent> TPA_CHECK_WORLD = register("teleport/tpa_check_world", builder ->
        builder);
    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}
