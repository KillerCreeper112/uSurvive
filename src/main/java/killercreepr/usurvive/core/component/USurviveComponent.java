package killercreepr.usurvive.core.component;

import killercreepr.crux.api.component.DataComponentType;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.usurvive.api.mechanics.Grapple;

import java.util.function.UnaryOperator;

public class USurviveComponent {
    public static void register(){}
    public static final DataComponentType<Grapple> GRAPPLE = register("grapple", builder -> builder
        .persistTextParser(USurviveCompParsers.GRAPPLE.createInput(Crux.key("grapple"))));

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}
