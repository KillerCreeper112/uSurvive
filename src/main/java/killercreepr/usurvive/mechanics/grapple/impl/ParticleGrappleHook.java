package killercreepr.usurvive.mechanics.grapple.impl;

import killercreepr.usurvive.mechanics.grapple.GrappleHook;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public abstract class ParticleGrappleHook implements GrappleHook {
    protected final @NotNull Key key;
    public ParticleGrappleHook(@NotNull Key key) {
        this.key = key;
    }

    /*@NotNull
    @Override
    public ActiveGrappleHook throwNew(@NotNull GrappleContext ctx) {
        return new ActiveParticleGrappleHook(
            ctx.getLocation(), ctx.getId(), .04, 16, .5D, .3D,
            .5D, new ParticleBuilder(Particle.CRIT).extra(0D)
        );
    }*/

    @Override
    public @NotNull Key key() {
        return key;
    }
}
