package killercreepr.usurvive.core.entity.player.data;

import killercreepr.crux.api.math.CruxLocation;
import killercreepr.usurvive.api.entity.player.data.RandomTPData;
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
        return map.get(world);
    }

    @Override
    public void setLastTeleported(@NotNull Key world, @Nullable Pos location) {
        if(location == null){
            map.remove(world);
        }else map.put(world, location);
    }

    @Override
    public boolean hasLastTeleported(@NotNull Key world) {
        return map.containsKey(world);
    }

    @Override
    public Map<Key, Pos> asMap() {
        return map;
    }

    @Override
    public void setAllLastTeleported(Map<Key, Pos> map) {
        this.map.clear();
        if(map==null) return;
        this.map.putAll(map);
    }
}
