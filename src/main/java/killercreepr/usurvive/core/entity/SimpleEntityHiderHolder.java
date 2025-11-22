package killercreepr.usurvive.core.entity;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityDataHolder;
import killercreepr.usurvive.api.entity.hider.EntityHider;
import killercreepr.usurvive.api.entity.hider.HiddenEntities;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;

public class SimpleEntityHiderHolder extends EntityDataHolder implements EntityHider {
    public static final Key KEY = Crux.key("entity_hider");

    public SimpleEntityHiderHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }
    public SimpleEntityHiderHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }
    protected final Map<UUID, Reference<Entity>> hidden = new HashMap<>();
    protected final Collection<HiddenEntities> hiddenEntities = new HashSet<>();

    @Override
    public boolean isHidden(Entity e) {
        return hidden.containsKey(e.getUniqueId());
    }

    @Override
    public void hide(Entity e) {
        if(parent.value() instanceof Player p){
            if(p.equals(e)) return;
            p.hideEntity(Crux.getMainPlugin(), e);
            hidden.put(e.getUniqueId(), new WeakReference<>(e));
        }
    }

    @Override
    public void show(Entity e) {
        if(parent.value() instanceof Player p){
            if(p.equals(e)) return;
            p.showEntity(Crux.getMainPlugin(), e);
        }
        hidden.remove(e.getUniqueId());
    }

    @Override
    public boolean updateVisibility(Entity e) {
        if(e.getUniqueId().equals(parent.getUUID())) return false;
        if(shouldHide(e)){
            if(isHidden(e)) return false;
            hide(e);
            return true;
        }
        if(!isHidden(e)) return false;
        show(e);
        return true;
    }

    @Override
    public void updateVisibility() {
        if(!(parent.value() instanceof Player p)) return;
        for (Entity entity : findShouldHide()) {
            if(isHidden(entity)) continue;
            hide(entity);
        }
        hidden.values().removeIf(ref ->{
            var e = ref.get();
            if(e == null) return true;
            if(shouldHide(e)){
                return false;
            }
            p.showEntity(Crux.getMainPlugin(), e);
            return true;
        });
    }

    @Override
    public boolean shouldHide(Entity e) {
        if(e.getUniqueId().equals(parent.getUUID())) return false;
        for (HiddenEntities hiddenEntity : hiddenEntities) {
            if(hiddenEntity.shouldHide(e)) return true;
        }
        return false;
    }

    @Override
    public Collection<Entity> findShouldHide() {
        Collection<Entity> list = new HashSet<>();
        for (HiddenEntities hiddenEntity : hiddenEntities) {
            list.addAll(hiddenEntity.findShouldHide());
        }
        return list;
    }

    @Override
    public boolean addHiddenEntities(HiddenEntities hiddenEntities) {
        boolean x = this.hiddenEntities.add(hiddenEntities);
        if(!x) return false;
        updateVisibility();
        return true;
    }

    @Override
    public boolean removeHiddenEntities(HiddenEntities hiddenEntities) {
        boolean x = this.hiddenEntities.remove(hiddenEntities);
        if(!x) return false;
        updateVisibility();
        return true;
    }
}
