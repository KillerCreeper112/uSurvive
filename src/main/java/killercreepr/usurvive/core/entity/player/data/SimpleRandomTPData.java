package killercreepr.usurvive.core.entity.player.data;

import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.usurvive.api.entity.player.data.RandomTPData;
import killercreepr.usurvive.core.component.USurviveComponents;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SimpleRandomTPData implements RandomTPData {
    protected final Map<Key, Pos> map = new HashMap<>();

    @Nullable
    @Override
    public Pos getLastTeleported(@NotNull Key world) {
        update(world);
        Pos pos = map.get(world);
        if(pos == null) return null;
        if(pos.hasExpired()){
            map.remove(world);
            return null;
        }
        return pos;
    }

    @Override
    public void setLastTeleported(@NotNull Key world, @Nullable Pos location) {
        if(location == null){
            map.remove(world);
        }else map.put(world, location);
    }

    @Override
    public boolean hasLastTeleported(@NotNull Key world) {
        update(world);
        return getLastTeleported(world) != null;
    }

    @Override
    public Map<Key, Pos> asMap() {
        return map;
    }

    @Override
    public void setAllLastTeleported(Map<Key, Pos> map) {
        this.map.clear();
        if(map==null) return;
        map.forEach((world, pos) ->{
            if(pos.hasExpired()) return;
            this.map.put(world, pos);
        });
    }

    @Override
    public boolean update(Key key){
        Pos pos = map.get(key);
        if(pos==null) return false;
        if(pos.hasExpired()) return true;
        CruxWorld world = CruxCore.core().worldManager().getWorld(key);
        if(world == null) return false;
        Long time = world.get(USurviveComponents.WORLD_RANDOM_TELEPORT_TIME);
        return time == null || time != pos.getTime();
    }

    @Override
    public void update() {
        map.keySet().removeIf(key ->{
            Pos pos = map.get(key);
            if(pos == null) return true;
            if(pos.hasExpired()) return true;
            CruxWorld world = CruxCore.core().worldManager().getWorld(key);
            if(world == null) return false;
            Long time = world.get(USurviveComponents.WORLD_RANDOM_TELEPORT_TIME);
            return time == null || time != pos.getTime();
        });
    }
}
