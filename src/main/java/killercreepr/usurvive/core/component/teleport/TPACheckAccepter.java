package killercreepr.usurvive.core.component.teleport;

import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxteleport.api.component.TeleporterComponent;
import killercreepr.cruxteleport.api.teleport.TeleportBuildContext;
import killercreepr.cruxteleport.api.teleport.holder.EntityTeleportHolder;
import killercreepr.cruxteleport.api.teleport.module.TeleportModule;
import killercreepr.usurvive.api.tpa.TPARequest;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TPACheckAccepter implements TeleporterComponent, TeleportModule {
    protected final TPARequest request;
    protected final Entity accepter;
    protected final UUID worldStarted;
    protected final Entity target;

    public TPACheckAccepter(TPARequest request, Entity sender, Entity accepter) {
        this.request = request;
        this.accepter = accepter;
        this.target = request.isHere() ? sender : accepter;
        worldStarted = target.getWorld().getUID();
    }

    @Nullable
    @Override
    public TeleportModule buildTeleportModule(@NotNull TeleportBuildContext ctx) {
        return this;
    }

    @Override
    public void tick(@NotNull EntityTeleportHolder holder, @NotNull Entity e) {
        if(!CruxEntityUtil.isValid(target) || !target.getWorld().getUID().equals(worldStarted)){
            holder.fail(e);
        }
    }

    @Override
    public void load(@NotNull EntityTeleportHolder entityTeleportHolder, @NotNull Entity entity) {

    }

    @Override
    public void unload(@NotNull EntityTeleportHolder entityTeleportHolder) {

    }
}
