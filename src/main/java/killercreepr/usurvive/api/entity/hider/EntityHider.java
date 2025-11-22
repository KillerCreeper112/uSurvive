package killercreepr.usurvive.api.entity.hider;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.usurvive.core.entity.SimpleEntityHiderHolder;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.function.Consumer;

public interface EntityHider {
    static EntityHider entityHider(Entity e){
        return EntityMemory.getDataHolder(e, SimpleEntityHiderHolder.class);
    }
    static EntityHider applyEntityHider(Entity e, Consumer<EntityHider> consumer){
        var hider = EntityMemory.getDataHolder(e, SimpleEntityHiderHolder.class);
        if(hider == null) return null;
        consumer.accept(hider);
        return hider;
    }

    boolean isHidden(Entity e);

    void hide(Entity e);
    void show(Entity e);
    //Collection<UUID> getHidden();

    boolean updateVisibility(Entity e);
    void updateVisibility();

    boolean shouldHide(Entity e);
    Collection<Entity> findShouldHide();

    boolean addHiddenEntities(HiddenEntities hiddenEntities);
    boolean removeHiddenEntities(HiddenEntities hiddenEntities);
}
