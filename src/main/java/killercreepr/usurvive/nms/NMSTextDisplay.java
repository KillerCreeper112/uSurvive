package killercreepr.usurvive.nms;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NMSTextDisplay extends BaseNMSEntity implements UTextDisplay{
    public void text(Component text){
        var metadata = new ClientboundSetEntityDataPacket(
            id,
            List.of(
                new SynchedEntityData.DataValue<>(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID.id(), EntityDataSerializers.INT, 20),
                new SynchedEntityData.DataValue<>(
                    23,
                    EntityDataSerializers.COMPONENT,
                    PaperAdventure.asVanilla(
                        text
                    )
                )
            )
        );

        forEachAudience(p ->{
            var connection = ((CraftPlayer)p).getHandle().connection;
            connection.send(metadata);
        });
    }

    public void spawn(Location loc){
        ClientboundAddEntityPacket add = new ClientboundAddEntityPacket(
            id,
            UUID.randomUUID(),
            loc.getX(),
            loc.getY(),
            loc.getZ(),
            loc.getPitch(),
            loc.getYaw(),
            EntityTypes.TEXT_DISPLAY,
            0,
            Vec3.ZERO,
            0.0
        );

        var metadata = new ClientboundSetEntityDataPacket(
            id,
            List.of(
                new SynchedEntityData.DataValue<>(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID.id(), EntityDataSerializers.INT, 20)
                )
            );

        var teleport = new ClientboundTeleportEntityPacket(
            id,
            new PositionMoveRotation(
                new Vec3(
                    loc.getX(),
                    loc.getY(),
                    loc.getZ()
                ),
                Vec3.ZERO,
                loc.getYaw(),
                loc.getPitch()
            ),
            Set.of(),
            false
        );

        var bundle = new ClientboundBundlePacket(List.of(add, metadata, teleport));

        forEachAudience(p ->{
            var connection = ((CraftPlayer)p).getHandle().connection;
            connection.send(bundle);
        });
    }
}
