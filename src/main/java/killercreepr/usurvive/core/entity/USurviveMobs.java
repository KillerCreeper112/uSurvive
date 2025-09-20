package killercreepr.usurvive.core.entity;

import killercreepr.crux.core.Crux;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.registries.CruxEntityRegistries;
import killercreepr.usurvive.core.entity.mob.*;
import org.bukkit.entity.*;

import java.util.function.Consumer;

public class USurviveMobs {
    public static void register(){
        Class<? extends Mob>[] eventUndead = new Class[]{
            Zombie.class, Stray.class, Husk.class, Bogged.class, Skeleton.class
        };
        registerVariants(
            type ->{
                CruxEntityRegistries.ENTITIES.register(
                    new PartyBurstMob(Crux.key("party_burst/" + type.getSimpleName().toLowerCase()),
                        type, 20, 12f, 7f, null)
                );
            },
            eventUndead
        );

        registerVariants(
            type ->{
                CruxEntityRegistries.ENTITIES.register(
                    new PartyPopperMob(Crux.key("party_popper/" + type.getSimpleName().toLowerCase()),
                        type, 20, 5f, 6f,null)
                );
            },
            eventUndead
        );

        registerVariants(
            type ->{
                CruxEntityRegistries.ENTITIES.register(
                    new TNTTempestMob(Crux.key("tnt_tempest/" + type.getSimpleName().toLowerCase()),
                        type, 20, 15f, null)
                );
            },
            eventUndead
        );

        registerVariants(
            type ->{
                CruxEntityRegistries.ENTITIES.register(
                    new BoneFluteMob(Crux.key("bone_flute/" + type.getSimpleName().toLowerCase()),
                        type, 20, 15f, -1f,null)
                );
            },
            eventUndead
        );

        registerVariants(
            type ->{
                CruxEntityRegistries.ENTITIES.register(
                    new GlitterMob(Crux.key("glitter/" + type.getSimpleName().toLowerCase()), type)
                );
            },
            eventUndead
        );

        registerVariants(
            type ->{
                CruxEntityRegistries.ENTITIES.register(
                    new ConfettiMob(Crux.key("confetti/" + type.getSimpleName().toLowerCase()), type,
                        20, 15f, -1f,null)
                );
            },
            eventUndead
        );
    }
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


    public static final CruxMob GUST_TNT = CruxEntityRegistries.ENTITIES.register(new GustTNT(Crux.key("gust_tnt")));
    public static final CruxMob STICKY_CHARGE = CruxEntityRegistries.ENTITIES.register(new StickyCharge(Crux.key("sticky_charge")));
    public static final CruxMob SMALL_TNT = CruxEntityRegistries.ENTITIES.register(new SmallTNT(Crux.key("small_tnt")));
    public static final CruxMob GLOOMSTING_PROJECTILE = CruxEntityRegistries.ENTITIES.register(new GloomstingProjectile(Crux.key("gloomsting_projectile")));

    public static final CruxMob WHISPERSHADE = CruxEntityRegistries.ENTITIES.register(new Whispershade(Crux.key("whispershade")));
    public static final CruxMob WHIMLET = CruxEntityRegistries.ENTITIES.register(new Whimlet(Crux.key("whimlet")));
    public static final CruxMob VOID_LURKER = CruxEntityRegistries.ENTITIES.register(new VoidLurker(Crux.key("void_lurker")));
    public static final CruxMob SPETTLE = CruxEntityRegistries.ENTITIES.register(new Spettle(Crux.key("spettle")));
    public static final CruxMob SHADE_STAG = CruxEntityRegistries.ENTITIES.register(new ShadeStag(Crux.key("shade_stag")));
    public static final CruxMob LURKSCALE = CruxEntityRegistries.ENTITIES.register(new Lurkscale(Crux.key("lurkscale")));
    public static final CruxMob BUZZROT = CruxEntityRegistries.ENTITIES.register(new Buzzrot(Crux.key("buzzrot")));
    public static final CruxMob BUZZROT_BEE = CruxEntityRegistries.ENTITIES.register(new BuzzrotBee(Crux.key("buzzrot_bee")));
    public static final CruxMob ZOMBIE_EAT = CruxEntityRegistries.ENTITIES.register(new ZombieEat(Crux.key("zombie_eat")));

    public static final CruxMob MIRRORSTRIKE = CruxEntityRegistries.ENTITIES.register(new Mirrorstrike(Crux.key("mirrorstrike")));

    public static final RemnantSpirit REMNANT_SPIRIT = CruxEntityRegistries.ENTITIES.register(new RemnantSpirit(Crux.key("remnant_spirit")));
    public static final CruxMob SNARE_ENCHANT = CruxEntityRegistries.ENTITIES.register(new SnareEnchant(Crux.key("snare_enchant")));
    public static final CruxMob BIGER = CruxEntityRegistries.ENTITIES.register(new Biger(Crux.key("biger")));

    public static final CruxMob AGGRESSIVE_ZOMBIE_HORSE = CruxEntityRegistries.ENTITIES.register(new AggressiveZombieHorse(Crux.key("aggressive_zombie_horse")));
    public static final CruxMob AGGRESSIVE_SKELETON_HORSE = CruxEntityRegistries.ENTITIES.register(new AggressiveSkeletonHorse(Crux.key("aggressive_skeleton_horse")));

    private static void registerVariants(Consumer<Class<? extends Mob>> consumer, Class<? extends Mob>... types){
        for(var clazz : types){
            consumer.accept(clazz);
        }
    }
}
