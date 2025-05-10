package killercreepr.usurvive.core.entity;

import killercreepr.crux.core.Crux;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.registries.CruxEntityRegistries;
import killercreepr.usurvive.core.entity.mob.*;

public class USurviveMobs {
    public static void register(){}
    public static final Echo ECHO = CruxEntityRegistries.ENTITIES.register(new Echo());
    public static final CruxMob SCULK_BLIGHT = CruxEntityRegistries.ENTITIES.register(new SculkBlight(Crux.key("sculk_blight")));
    public static final GrappleHookMob GRAPPLE_HOOK = CruxEntityRegistries.ENTITIES.register(new GrappleHookMob(Crux.key("grapple_hook")));
    public static final CruxMob DOWNMAN = CruxEntityRegistries.ENTITIES.register(new Downman(Crux.key("downman")));
    public static final CruxMob ORBIT_CONDUIT = CruxEntityRegistries.ENTITIES.register(new OrbitConduit(Crux.key("orbit_conduit")));
    public static final CruxMob STICK_BUG = CruxEntityRegistries.ENTITIES.register(new StickBug(Crux.key("stick_bug")));

    public static final CruxMob SLEEPER_OBJECT = CruxEntityRegistries.ENTITIES.register(new SleeperObjectEntity(Crux.key("sleeper_object")));
    public static final CruxMob SLEEPER_DISPLAY = CruxEntityRegistries.ENTITIES.register(new SleeperDisplayEntity(Crux.key("sleeper_display")));
    public static final CruxMob SLEEP_TARGET = CruxEntityRegistries.ENTITIES.register(new SleeperTargetEntity(Crux.key("sleeper_target")));

    public static final CruxMob VANE = CruxEntityRegistries.ENTITIES.register(new Vane(Crux.key("vane")));
    public static final CruxMob STARE = CruxEntityRegistries.ENTITIES.register(new Stare(Crux.key("stare")));
    public static final CruxMob SKELETON_WOLF = CruxEntityRegistries.ENTITIES.register(new SkeletonWolf(Crux.key("skeleton_wolf")));
    public static final CruxMob SKELETON_CHICKEN = CruxEntityRegistries.ENTITIES.register(new SkeletonChicken(Crux.key("skeleton_chicken")));


    public static final CruxMob WHISPERSHADE = CruxEntityRegistries.ENTITIES.register(new Whispershade(Crux.key("whispershade")));
    public static final CruxMob WHIMLET = CruxEntityRegistries.ENTITIES.register(new Whimlet(Crux.key("whimlet")));
    public static final CruxMob VOID_LURKER = CruxEntityRegistries.ENTITIES.register(new VoidLurker(Crux.key("void_lurker")));
    public static final CruxMob SPETTLE = CruxEntityRegistries.ENTITIES.register(new Spettle(Crux.key("spettle")));
    public static final CruxMob SHADE_STAG = CruxEntityRegistries.ENTITIES.register(new ShadeStag(Crux.key("shade_stag")));
    public static final CruxMob LURKSCALE = CruxEntityRegistries.ENTITIES.register(new Lurkscale(Crux.key("lurkscale")));
    public static final CruxMob BUZZROT = CruxEntityRegistries.ENTITIES.register(new Buzzrot(Crux.key("buzzrot")));
}
