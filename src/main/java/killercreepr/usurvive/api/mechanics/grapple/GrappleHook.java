package killercreepr.usurvive.api.mechanics.grapple;

import killercreepr.usurvive.api.mechanics.grapple.context.GrappleContext;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public interface GrappleHook extends Keyed {
    @NotNull ActiveGrappleHook throwNew(@NotNull GrappleContext ctx);
}
