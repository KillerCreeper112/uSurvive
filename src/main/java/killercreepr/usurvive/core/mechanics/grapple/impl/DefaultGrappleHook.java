package killercreepr.usurvive.core.mechanics.grapple.impl;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import killercreepr.usurvive.api.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.mechanics.grapple.GrappleContext;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DefaultGrappleHook extends EntityGrappleHook{
    protected final @NotNull NumberProvider pullForce;
    public DefaultGrappleHook(@NotNull Key key, @NotNull NumberProvider pullForce) {
        super(key);
        this.pullForce = pullForce;
    }

    @NotNull
    public NumberProvider getPullForce() {
        return pullForce;
    }

    @NotNull
    @Override
    public LivingEntity spawnEntity(@NotNull GrappleContext ctx) {
        Entity owner = ctx.getOwner();
        return USurviveMobs.GRAPPLE_HOOK.spawnAt(Slime.class, ctx.getLocation(), e ->{
            ModelEntity modelEntity = new ModelEntity(e, "grapple_hook");
            modelEntity.getModel().getLeashManager().get().connectLeash(owner, "leash");
            e.setLeashHolder(owner);
            e.setSize(0);
            Vector vel = CruxEntityUtil.getEyeOrLocation(owner).getDirection().multiply(ctx.getForce());
            e.setVelocity(vel);
        });
    }

    @NotNull
    @Override
    public ActiveGrappleHook throwNew(@NotNull GrappleContext ctx) {
        LivingEntity hook = spawnEntity(ctx);
        return new ActiveEntityGrappleHook(hook, ctx.getId(), pullForce.value().doubleValue());
    }
}
