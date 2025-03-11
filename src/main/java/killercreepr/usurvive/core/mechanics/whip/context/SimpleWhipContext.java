package killercreepr.usurvive.core.mechanics.whip.context;

import killercreepr.usurvive.api.mechanics.whip.context.WhipContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SimpleWhipContext implements WhipContext {
    protected final @NotNull Location location;
    protected final @NotNull Entity owner;
    protected final int id;
    protected final double throwForce;
    protected final int pierce;

    public SimpleWhipContext(@NotNull Location location, @NotNull Entity owner, int id, double throwForce, int pierce) {
        this.location = location;
        this.owner = owner;
        this.id = id;
        this.throwForce = throwForce;
        this.pierce = pierce;
    }

    @Override
    public double getThrowForce() {
        return throwForce;
    }

    @Override
    public int getPierce() {
        return pierce;
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
