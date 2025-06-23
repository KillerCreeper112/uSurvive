package killercreepr.usurvive.api.mechanics.whip.context;

import killercreepr.crux.api.data.CruxKeyed;
import killercreepr.cruxattributes.api.attribute.CruxAttributeAccessor;
import killercreepr.usurvive.core.mechanics.whip.context.SimpleWhipContext;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WhipContext extends CruxKeyed {
    static WhipContext context(@NotNull Location location, @NotNull Entity owner, Key key,
                               double throwForce, int pierce, @Nullable CruxAttributeAccessor attributes){
        return new SimpleWhipContext(
            location, owner, key, throwForce, pierce,
            attributes
        );
    }

    double getThrowForce();
    int getPierce();

    @NotNull
    Location getLocation();

    @NotNull
    Entity getOwner();

    @Nullable CruxAttributeAccessor getAttributes();
}
