package killercreepr.usurvive.component;

import killercreepr.crux.Crux;
import killercreepr.crux.component.DataComponentType;
import killercreepr.crux.registries.CruxRegistries;
import killercreepr.usurvive.component.impl.OrbitConduitComponent;

import java.util.function.UnaryOperator;

public class USurviveComponents {
    public static final DataComponentType<OrbitConduitComponent> DEPOSIT_CONDUIT = register("deposit_conduit", builder ->
        builder);
    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}
