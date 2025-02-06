package killercreepr.usurvive.api.mechanics.grapple.context;

import killercreepr.usurvive.core.mechanics.grapple.context.SimpleGrappleContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface GrappleContext {
    static GrappleContext context(@NotNull Location location, @NotNull Entity owner, int id, double force){
        return new SimpleGrappleContext(location, owner, id, force);
    }

    double getForce();

    @NotNull
    Location getLocation();

    @NotNull
    Entity getOwner();

    int getId();
}
