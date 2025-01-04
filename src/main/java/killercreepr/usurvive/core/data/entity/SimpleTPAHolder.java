package killercreepr.usurvive.core.data.entity;

import killercreepr.crux.api.data.User;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.util.UserMap;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.usurvive.api.tpa.TPAHolder;
import killercreepr.usurvive.api.tpa.TPARequest;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleTPAHolder extends EntityTickedDataHolder implements TPAHolder {
    public static final Key KEY = Crux.key("tpa");
    public SimpleTPAHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }

    public SimpleTPAHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    protected final UserMap<User, TPARequest> requests = UserMap.userMap(ConcurrentHashMap::new);

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Entity e) {
        return super.shouldRemoveFromMemory(e) || requests().isEmpty();
    }

    public UserMap<User, TPARequest> requests(){
        requests.removeIf(TPARequest::isValid);
        return requests;
    }

    @Override
    public void addRequest(@NotNull TPARequest request) {
        requests().put(request.sender(), request);
    }

    @Nullable
    @Override
    public TPARequest getRequest(@NotNull UUID from) {
        return requests().getFromUser(from);
    }

    @Nullable
    @Override
    public TPARequest getRequest(@NotNull String from) {
        return requests().getFromUser(from);
    }

    @Nullable
    @Override
    public TPARequest removeRequest(@NotNull UUID from) {
        return requests().removeUser(from);
    }

    @Nullable
    @Override
    public TPARequest removeRequest(@NotNull String from) {
        return requests().removeUser(from);
    }

    @NotNull
    @Override
    public Collection<TPARequest> getRequests() {
        return requests().values();
    }

    @Override
    public void tick(@NotNull Entity entity) {

    }
}
