package killercreepr.usurvive.mechanics.grapple.impl;

import killercreepr.usurvive.mechanics.grapple.ActiveGrappleHook;
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

    @NotNull
    @Override
    public ActiveGrappleHook throwNew(@NotNull GrappleContext ctx) {
        LivingEntity hook = spawnEntity(ctx);
        return new ActiveEntityGrappleHook(hook, ctx.getId());
    }

    @Override
    public @NotNull Key key() {
        return key;
    }

    protected abstract @NotNull LivingEntity spawnEntity(@NotNull GrappleContext ctx);
}
