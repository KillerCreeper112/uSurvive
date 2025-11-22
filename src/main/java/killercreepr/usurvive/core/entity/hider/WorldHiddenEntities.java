package killercreepr.usurvive.core.entity.hider;

import killercreepr.crux.api.data.Holder;
import killercreepr.usurvive.api.entity.hider.HiddenEntities;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class WorldHiddenEntities implements HiddenEntities {
    protected final Holder<World> world;
    protected final Predicate<Entity> filter;

    public WorldHiddenEntities(Holder<World> world, Predicate<Entity> filter) {
        this.world = world;
        this.filter = filter;
    }

    @Override
    public boolean shouldHide(Entity e) {
        return e.getWorld().equals(world.value()) && (filter == null || filter.test(e));
    }

    @Override
    public Collection<Entity> findShouldHide() {
        var w = world.value();
        if(w == null) return Set.of();
        if(filter == null) return w.getEntities();
        Collection<Entity> set = new HashSet<>();
        for (Entity entity : w.getEntities()) {
            if(!filter.test(entity)) continue;
            set.add(entity);
        }
        return set;
    }
}
