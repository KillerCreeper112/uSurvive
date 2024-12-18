package killercreepr.usurvive.core.mechanics.grapple.standard;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.usurvive.api.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.api.mechanics.grapple.GrappleHook;
import killercreepr.usurvive.core.mechanics.grapple.GrappleContext;
import killercreepr.usurvive.core.mechanics.grapple.impl.*;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public class StandardGrappleHooks {
    public static GrappleHook DEFAULT = new DefaultGrappleHook(
        Crux.key("default"),
        NumberProvider.constant(2.25D),
        NumberProvider.constant(.02D)
    );
    public static GrappleHook CONTROLLED = new ControlledGrappleHook(
        Crux.key("controlled"),
        NumberProvider.constant(2.25D),
        NumberProvider.constant(.02D)
    );
    public static GrappleHook PUFFERFISH = new PufferFishGrappleHook(
        Crux.key("pufferfish"),
        NumberProvider.constant(2.25D),
        NumberProvider.constant(.02D)
    );
    public static GrappleHook CRIT = new ParticleGrappleHook(
        Crux.key("crit")
    ) {
        @NotNull
        @Override
        public ActiveGrappleHook throwNew(@NotNull GrappleContext ctx) {
            return new ActiveParticleGrappleHook(
                ctx.getLocation(), ctx.getId(), (.02D * ctx.getForce()),
                12, .5D, .3D, .5D,
                new ParticleBuilder(Particle.CRIT).extra(0D)
            );
        }
    };
    public static void register(@NotNull KeyedRegistry<GrappleHook> registry){
        registry.register(PUFFERFISH);
        registry.register(CRIT);
        registry.register(DEFAULT);
        registry.register(CONTROLLED);
    }
}
