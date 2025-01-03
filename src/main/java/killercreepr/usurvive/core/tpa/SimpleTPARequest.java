package killercreepr.usurvive.core.tpa;

import killercreepr.cruxteleport.api.teleport.CruxTeleport;
import killercreepr.cruxteleport.api.teleport.CruxTeleporter;
import killercreepr.usurvive.api.tpa.TPARequest;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.util.UUID;

public class SimpleTPARequest implements TPARequest {
    protected final UUID from;
    protected final String name;
    protected final Reference<Entity> sender;
    protected final CruxTeleport teleport;
    protected final long timeSent;

    public SimpleTPARequest(UUID from, String name, Reference<Entity> sender, CruxTeleport teleport, long timeSent) {
        this.from = from;
        this.name = name;
        this.sender = sender;
        this.teleport = teleport;
        this.timeSent = timeSent;
    }

    @Override
    public void onAccept(Entity sender) {
        Entity e = this.sender.get();
        if(e == null) throw new IllegalStateException("Sender from TPA Request is no longer present!");
        CruxTeleporter.teleporter().scheduleTeleport(e, teleport);
    }

    @NotNull
    @Override
    public UUID from() {
        return from;
    }

    @Nullable
    @Override
    public String name() {
        return "";
    }

    @NotNull
    @Override
    public CruxTeleport teleport() {
        return teleport;
    }

    @Override
    public long timeSent() {
        return timeSent;
    }

    @Override
    public boolean isValid() {
        return sender.get() != null;//todo check time
    }
}
