package killercreepr.usurvive.api.entity.hider;

import killercreepr.crux.api.data.Holder;
import killercreepr.usurvive.core.entity.hider.WorldClassHiddenEntities;
import killercreepr.usurvive.core.entity.hider.WorldHiddenEntities;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.function.Predicate;

public interface HiddenEntities {
    static HiddenEntities world(Holder<World> world){
        return world(world, null);
    }

    static HiddenEntities world(Holder<World> world, Predicate<Entity> filter){
        return new WorldHiddenEntities(world, filter);
    }

    static <T extends Entity> HiddenEntities worldType(Class<T> type, Holder<World> world, Predicate<T> filter){
        return new WorldClassHiddenEntities<>(world, type, filter);
    }

    static <T extends Entity> HiddenEntities worldType(Class<T> type, Holder<World> world){
        return worldType(type, world, null);
    }

    boolean shouldHide(Entity e);
    Collection<Entity> findShouldHide();
}
