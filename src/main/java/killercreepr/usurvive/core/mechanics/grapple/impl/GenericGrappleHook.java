package killercreepr.usurvive.core.mechanics.grapple.impl;

import killercreepr.usurvive.api.mechanics.grapple.GrappleHook;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public abstract class GenericGrappleHook implements GrappleHook {
    protected final @NotNull Key key;
    public GenericGrappleHook(@NotNull Key key) {
        this.key = key;
    }

    @Override
    public @NotNull Key key() {
        return key;
    }
}
