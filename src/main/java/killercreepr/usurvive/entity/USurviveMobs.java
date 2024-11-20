package killercreepr.usurvive.entity;

import killercreepr.crux.core.Crux;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.registries.CruxEntityRegistries;
import killercreepr.usurvive.entity.mob.Downman;
import killercreepr.usurvive.entity.mob.Echo;
import killercreepr.usurvive.entity.mob.OrbitConduit;
import killercreepr.usurvive.entity.mob.SculkBlight;

public class USurviveMobs {
    public static void register(){}
    public static final Echo ECHO = CruxEntityRegistries.ENTITIES.register(new Echo());
    public static final CruxMob SCULK_BLIGHT = CruxEntityRegistries.ENTITIES.register(new SculkBlight(Crux.key("sculk_blight")));
    public static final GrappleHookMob GRAPPLE_HOOK = CruxEntityRegistries.ENTITIES.register(new GrappleHookMob(Crux.key("grapple_hook")));
    public static final CruxMob DOWNMAN = CruxEntityRegistries.ENTITIES.register(new Downman(Crux.key("downman")));
    public static final CruxMob ORBIT_CONDUIT = CruxEntityRegistries.ENTITIES.register(new OrbitConduit(Crux.key("orbit_conduit")));
}
