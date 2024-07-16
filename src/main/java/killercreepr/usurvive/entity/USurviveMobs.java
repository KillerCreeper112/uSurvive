package killercreepr.usurvive.entity;

import killercreepr.cruxentities.registries.CruxEntityRegistries;
import killercreepr.usurvive.entity.mob.Echo;

public class USurviveMobs {
    public static void register(){}
    public static final Echo ECHO = CruxEntityRegistries.ENTITIES.register(new Echo());
}
