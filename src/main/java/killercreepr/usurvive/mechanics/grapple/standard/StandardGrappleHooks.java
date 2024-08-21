package killercreepr.usurvive.mechanics.grapple.standard;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.Crux;
import killercreepr.crux.registry.KeyedRegistry;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.usurvive.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.mechanics.grapple.GrappleContext;
import killercreepr.usurvive.mechanics.grapple.GrappleHook;
import killercreepr.usurvive.mechanics.grapple.impl.ActiveParticleGrappleHook;
import killercreepr.usurvive.mechanics.grapple.impl.ParticleGrappleHook;
import killercreepr.usurvive.mechanics.grapple.impl.PufferFishGrappleHook;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public class StandardGrappleHooks {
    public static PufferFishGrappleHook PUFFERFISH = new PufferFishGrappleHook(
        Crux.key("pufferfish"),
        NumberProvider.constant(2.25D),
        NumberProvider.constant(.02D)
    );
    public static ParticleGrappleHook CRIT = new ParticleGrappleHook(
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
    }
}
