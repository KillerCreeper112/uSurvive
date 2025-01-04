package killercreepr.usurvive.api.tpa;

import killercreepr.crux.api.data.User;
import killercreepr.crux.core.location.DynamicLocation;
import killercreepr.cruxteleport.api.teleport.CruxTeleport;
import killercreepr.usurvive.core.tpa.SimpleTPARequest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public interface TPARequest {
    static TPARequest request(HumanEntity from, HumanEntity to){
        return request(from, to, System.currentTimeMillis());
    }

    static TPARequest request(HumanEntity from, HumanEntity to, long timeSent){
        CruxTeleport.Builder builder = CruxTeleport.builder()
            .teleportTo(DynamicLocation.createEntity(to))
            .waitingTime(100)
            ;
        return request(from, builder, timeSent);
    }

    static TPARequest request(HumanEntity from, CruxTeleport.Builder teleport){
        return request(from, teleport, System.currentTimeMillis());
    }

    static TPARequest request(HumanEntity from, CruxTeleport.Builder teleport, long timeSent){
        return request(
            User.user(from), new WeakReference<>(from), teleport, timeSent
        );
    }

    static TPARequest request(User user, Reference<Entity> sender, CruxTeleport.Builder teleport, long timeSent){
        return new SimpleTPARequest(user, sender, teleport, timeSent);
    }

    void onAccept(Entity sender);

    long timeAccepted();
    void setAccepted(long time);
    @NotNull User sender();
    @Nullable Entity getSender();
    @NotNull CruxTeleport.Builder teleport();
    long timeSent();
    boolean isValid();
}
