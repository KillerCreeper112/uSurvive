package killercreepr.usurvive.core.mechanics.whip.context;

import killercreepr.cruxattributes.api.attribute.CruxAttributeAccessor;
import killercreepr.usurvive.api.mechanics.whip.context.WhipContext;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleWhipContext implements WhipContext {
    protected final @NotNull Location location;
    protected final @NotNull Entity owner;
    protected final Key key;
    protected final double throwForce;
    protected final int pierce;
    protected final @Nullable CruxAttributeAccessor attributes;

    public SimpleWhipContext(@NotNull Location location, @NotNull Entity owner, Key key, double throwForce, int pierce, @Nullable CruxAttributeAccessor attributes) {
        this.location = location;
        this.owner = owner;
        this.key = key;
        this.throwForce = throwForce;
        this.pierce = pierce;
        this.attributes = attributes;
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

    @NotNull
    @Override
    public Key key() {
        return key;
    }

    @Nullable
    @Override
    public CruxAttributeAccessor getAttributes() {
        return attributes;
    }
}
