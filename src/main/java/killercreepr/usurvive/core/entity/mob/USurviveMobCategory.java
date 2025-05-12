package killercreepr.usurvive.core.entity.mob;

import killercreepr.crux.core.Crux;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleMobCategory;
import killercreepr.cruxentities.registries.CruxEntityRegistries;

public class USurviveMobCategory {
    public static void register(){}
    public static final MobCategory ELDRITCH = CruxEntityRegistries.MOB_CATEGORY.register(new SimpleMobCategory(Crux.key("eldritch")));
}
