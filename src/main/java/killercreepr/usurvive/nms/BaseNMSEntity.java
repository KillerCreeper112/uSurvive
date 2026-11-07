package killercreepr.usurvive.nms;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.core.util.CruxMath;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class BaseNMSEntity implements NMSEntity {
    protected final int id = CruxMath.random(0, Integer.MAX_VALUE);//Entity.nextEntityId();
    protected final Collection<Holder<Player>> audience = new HashSet<>();
    @Override
    public Collection<Holder<Player>> getAudience() {
        return audience;
    }

    @Override
    public void teleport(Location loc) {
        ClientboundTeleportEntityPacket packet = new ClientboundTeleportEntityPacket(
            id, new PositionMoveRotation(new Vec3(loc.getX(), loc.getY(), loc.getZ()), Vec3.ZERO, loc.getPitch(), loc.getYaw()),
            Set.of(), false
        );
        forEachNMSConnection(connection ->{
            connection.send(packet);
        });
    }

    public void forEachNMSPlayer(Consumer<ServerPlayer> consumer){
        forEachAudience(p ->{
            consumer.accept(((CraftPlayer)p).getHandle());
        });
    }

    public void forEachNMSConnection(Consumer<ServerGamePacketListenerImpl> consumer){
        forEachAudience(p ->{
            consumer.accept(((CraftPlayer)p).getHandle().connection);
        });
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void addAudience(Player... audience) {
        for (Player player : audience) {
            this.audience.add(new PlayerHolder(player));
        }
    }

    @Override
    public void removeAudience(Player... audience) {
        for (Player player : audience) {
            this.audience.remove(new PlayerHolder(player));
        }
    }
}
