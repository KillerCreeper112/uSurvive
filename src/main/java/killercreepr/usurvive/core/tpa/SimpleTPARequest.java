package killercreepr.usurvive.core.tpa;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.data.User;
import killercreepr.crux.api.text.tags.TagsPrefixBuilder;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxteleport.api.teleport.CruxTeleport;
import killercreepr.cruxteleport.api.teleport.CruxTeleporter;
import killercreepr.usurvive.api.tpa.TPARequest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;

public class SimpleTPARequest implements TPARequest {
    protected final User from;
    protected final Reference<Entity> sender;
    protected final CruxTeleport.Builder teleport;
    protected final long timeSent;
    protected long timeAccepted = -1;
    protected final boolean tpHere;

    public SimpleTPARequest(User from, Reference<Entity> sender, CruxTeleport.Builder teleport, long timeSent, boolean tpHere) {
        this.from = from;
        this.sender = sender;
        this.teleport = teleport;
        this.timeSent = timeSent;
        this.tpHere = tpHere;
    }

    @Override
    public void onAccept(Entity sender) {
        Entity e = this.sender.get();
        if(e == null) throw new IllegalStateException("Sender from TPA Request is no longer present!");
        setAccepted(System.currentTimeMillis());

        if(tpHere){
            if(sender instanceof HumanEntity h){
                CruxCore.core().cruxMenus().menuRegistry().menuHolders().get(Crux.key("tpaccept_confirm"))
                    .open(h, DataExchange.builder()
                            .put("accepter", e)
                            .put(this).build(),
                        TagContainer.merged().hook(e, TagsPrefixBuilder.overwriteBase("accepter_")));
            }else CruxTeleporter.teleporter().scheduleTeleport(sender, teleport.build());
            return;
        }

        if(e instanceof HumanEntity h){
            CruxCore.core().cruxMenus().menuRegistry().menuHolders().get(Crux.key("tpaccept_confirm"))
                .open(h, DataExchange.builder()
                    .put("accepter", sender)
                    .put(this).build(),
                    TagContainer.merged().hook(sender, TagsPrefixBuilder.overwriteBase("accepter_")));
        }else CruxTeleporter.teleporter().scheduleTeleport(e, teleport.build());
    }

    @Override
    public long timeAccepted() {
        return timeAccepted;
    }

    @Override
    public void setAccepted(long time) {
        this.timeAccepted = time;
    }

    @NotNull
    @Override
    public User sender() {
        return from;
    }

    @Nullable
    @Override
    public Entity getSender() {
        return sender.get();
    }

    @NotNull
    @Override
    public CruxTeleport.Builder teleport() {
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

    @Override
    public boolean isHere() {
        return tpHere;
    }
}
