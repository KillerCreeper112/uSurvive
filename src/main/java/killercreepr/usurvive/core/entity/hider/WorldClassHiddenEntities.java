package killercreepr.usurvive.core.entity.hider;

import killercreepr.crux.api.data.Holder;
import killercreepr.usurvive.api.entity.hider.HiddenEntities;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class WorldClassHiddenEntities<T extends Entity> implements HiddenEntities {
    protected final Holder<World> world;
    protected final Class<T> type;
    protected final Predicate<T> filter;

    public WorldClassHiddenEntities(Holder<World> world, Class<T> type, Predicate<T> filter) {
        this.world = world;
        this.type = type;
        this.filter = filter;
    }

    @Override
    public boolean shouldHide(Entity e) {
        return e.getWorld().equals(world.value()) && type.isAssignableFrom(e.getClass()) && (filter == null || filter.test(type.cast(e)));
    }

    @Override
    public Collection<Entity> findShouldHide() {
        var w = world.value();
        if(w == null) return Set.of();
        if(filter == null) return (Collection<Entity>) w.getEntitiesByClass(type);
        Collection<Entity> set = new HashSet<>();
        for (T entity : w.getEntitiesByClass(type)) {
            if(!filter.test(entity)) continue;
            set.add(entity);
        }
        return set;
    }
}
