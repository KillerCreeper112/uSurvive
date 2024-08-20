package killercreepr.usurvive.mechanics.grapple.impl;

import killercreepr.usurvive.mechanics.grapple.GrappleContext;
import killercreepr.usurvive.mechanics.grapple.GrappleHook;
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
