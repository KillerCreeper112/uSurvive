package killercreepr.usurvive.mechanics.grapple.impl;

import killercreepr.crux.util.CruxEntity;
import killercreepr.usurvive.entity.USurviveMobs;
import killercreepr.usurvive.mechanics.grapple.GrappleContext;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PufferFish;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PufferFishGrappleHook extends EntityGrappleHook{
    public PufferFishGrappleHook(@NotNull Key key) {
        super(key);
    }

    @NotNull
    @Override
    public LivingEntity spawnEntity(@NotNull GrappleContext ctx) {
        Entity owner = ctx.getOwner();
        return USurviveMobs.GRAPPLE_HOOK.spawnAt(PufferFish.class, ctx.getLocation(), e ->{
            e.setLeashHolder(owner);
            e.setPuffState(3);
            Vector vel = CruxEntity.getEyeOrLocation(owner).getDirection().multiply(ctx.getForce());
            e.setVelocity(vel);
        });
    }
}
