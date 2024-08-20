package killercreepr.usurvive.mechanics.grapple;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class Grapple {
    protected final @NotNull Key grappleHook;
    public Grapple(@NotNull Key grappleHook) {
        this.grappleHook = grappleHook;
    }

    @NotNull
    public Key getGrappleHook() {
        return grappleHook;
    }
}
