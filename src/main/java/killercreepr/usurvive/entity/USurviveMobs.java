package killercreepr.usurvive.entity;

import killercreepr.crux.Crux;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.registries.CruxEntityRegistries;
import killercreepr.usurvive.entity.mob.Echo;
import killercreepr.usurvive.entity.mob.SculkBlight;

public class USurviveMobs {
    public static void register(){}
    public static final Echo ECHO = CruxEntityRegistries.ENTITIES.register(new Echo());
    public static final CruxMob SCULK_BLIGHT = CruxEntityRegistries.ENTITIES.register(new SculkBlight(Crux.key("sculk_blight")));
    public static final GrappleHookMob GRAPPLE_HOOK = CruxEntityRegistries.ENTITIES.register(new GrappleHookMob(Crux.key("grapple_hook")));
}
