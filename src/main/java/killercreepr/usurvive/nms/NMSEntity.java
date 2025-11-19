package killercreepr.usurvive.nms;

import killercreepr.crux.api.data.Holder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.function.Consumer;

public interface NMSEntity {
    Collection<Holder<Player>> getAudience();
    void addAudience(Player... audience);
    void removeAudience(Player... audience);
    void spawn(Location loc);
    void teleport(Location loc);
    int getId();

    default void forEachAudience(Consumer<Player> consumer){
        getAudience().forEach(holder ->{
            var got = holder.value();
            if(got != null) consumer.accept(got);
        });
    }
}
