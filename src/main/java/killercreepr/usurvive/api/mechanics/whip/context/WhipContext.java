package killercreepr.usurvive.api.mechanics.whip.context;

import killercreepr.usurvive.core.mechanics.whip.context.SimpleWhipContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface WhipContext {
    static WhipContext context(@NotNull Location location, @NotNull Entity owner, int id,
                               double throwForce, int pierce){
        return new SimpleWhipContext(
            location, owner, id, throwForce, pierce
        );
    }

    double getThrowForce();
    int getPierce();

    @NotNull
    Location getLocation();

    @NotNull
    Entity getOwner();

    int getId();
}
