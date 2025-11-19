package killercreepr.usurvive.nms;

import killercreepr.crux.api.data.Holder;
import org.bukkit.entity.Player;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.UUID;

public class PlayerHolder implements Holder<Player> {
    protected final UUID uuid;
    protected final Reference<Player> reference;
    public PlayerHolder(Player p){
        uuid = p.getUniqueId();
        this.reference = new WeakReference<>(p);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PlayerHolder that)) return false;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    @Override
    public String toString() {
        return "PlayerHolder{" +
            "uuid=" + uuid +
            '}';
    }

    @Override
    public Player value() {
        return reference.get();
    }
}
