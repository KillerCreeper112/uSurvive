package killercreepr.usurvive.core.mechanics.grapple.impl;

import killercreepr.usurvive.api.mechanics.grapple.GrappleHook;
import killercreepr.usurvive.core.mechanics.grapple.GrappleContext;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public abstract class EntityGrappleHook implements GrappleHook {
    protected final @NotNull Key key;
    public EntityGrappleHook(@NotNull Key key) {
        this.key = key;
    }

    @Override
    public @NotNull Key key() {
        return key;
    }

    protected abstract @NotNull LivingEntity spawnEntity(@NotNull GrappleContext ctx);
}
