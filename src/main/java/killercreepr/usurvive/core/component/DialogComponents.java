package killercreepr.usurvive.core.component;

import killercreepr.crux.api.component.DataComponentType;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;

import java.util.function.UnaryOperator;

public class DialogComponents {
    public static void register(){};
    public static final DataComponentType<Boolean> PRE_PARSE_FOR_SINGLE_AUDIENCE = register("pre_parse_for_single_audience", builder -> builder);

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key("dialog/" + id), builderOperator.apply(DataComponentType.builder()).build());
    }
}