package killercreepr.usurvive.mechanics.grapple;

import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public interface GrappleHook extends Keyed {
    @NotNull ActiveGrappleHook throwNew(@NotNull GrappleContext ctx);
    int defaultMaxHooks();
}
