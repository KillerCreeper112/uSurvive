package killercreepr.usurvive.mechanics.grapple.impl;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.usurvive.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.mechanics.grapple.GrappleContext;
import killercreepr.usurvive.mechanics.grapple.GrappleHook;
import net.kyori.adventure.key.Key;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public class ParticleGrappleHook implements GrappleHook {
    @NotNull
    @Override
    public ActiveGrappleHook throwNew(@NotNull GrappleContext ctx) {
        return new ActiveParticleGrappleHook(
            ctx.getLocation(), ctx.getId(), .04, 16, .5D, .3D,
            .5D, new ParticleBuilder(Particle.CRIT).extra(0D)
        );
    }

    @Override
    public int defaultMaxHooks() {
        return 3;
    }

    @Override
    public @NotNull Key key() {
        return null;
    }
}
