package killercreepr.usurvive.core.mechanics.grapple.context;

import killercreepr.usurvive.api.mechanics.grapple.context.GrappleContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SimpleGrappleContext implements GrappleContext {
    protected final @NotNull Location location;
    protected final @NotNull Entity owner;
    protected final int id;
    protected final double force;

    public SimpleGrappleContext(@NotNull Location location, @NotNull Entity owner, int id, double force) {
        this.location = location;
        this.owner = owner;
        this.id = id;
        this.force = force;
    }

    public double getForce() {
        return force;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public Entity getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }
}
