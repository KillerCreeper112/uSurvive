package killercreepr.usurvive.api.tpa;

import killercreepr.crux.core.location.DynamicLocation;
import killercreepr.cruxteleport.api.teleport.CruxTeleport;
import killercreepr.usurvive.core.tpa.SimpleTPARequest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.UUID;

public interface TPARequest {
    static TPARequest request(HumanEntity from, HumanEntity to){
        return request(from, to, System.currentTimeMillis());
    }

    static TPARequest request(HumanEntity from, HumanEntity to, long timeSent){
        CruxTeleport teleport = CruxTeleport.teleport(
            DynamicLocation.createEntity(to), 100
        );
        return request(from, teleport, timeSent);
    }

    static TPARequest request(HumanEntity from, CruxTeleport teleport){
        return request(from, teleport, System.currentTimeMillis());
    }

    static TPARequest request(HumanEntity from, CruxTeleport teleport, long timeSent){
        return request(
            from.getUniqueId(), from.getName(), new WeakReference<>(from), teleport, timeSent
        );
    }

    static TPARequest request(UUID from, String name, Reference<Entity> sender, CruxTeleport teleport, long timeSent){
        return new SimpleTPARequest(from, name, sender, teleport, timeSent);
    }

    void onAccept(Entity sender);

    @NotNull UUID from();
    @Nullable String name();
    @NotNull CruxTeleport teleport();
    long timeSent();
    boolean isValid();
}
