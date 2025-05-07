package killercreepr.usurvive.core.component.teleport;

import killercreepr.crux.api.math.CruxLocation;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxColor;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxteleport.api.component.TeleporterComponent;
import killercreepr.cruxteleport.api.teleport.TeleportBuildContext;
import killercreepr.cruxteleport.api.teleport.holder.EntityTeleportHolder;
import killercreepr.cruxteleport.api.teleport.module.TeleportModule;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.api.entity.player.data.RandomTPData;
import killercreepr.usurvive.core.component.USurviveComponents;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportSetLastTP implements TeleporterComponent, TeleportModule {
    @Nullable
    @Override
    public TeleportModule buildTeleportModule(@NotNull TeleportBuildContext teleportBuildContext) {
        return this;
    }

    @Override
    public boolean onSuccess(@NotNull EntityTeleportHolder holder, @NotNull Entity e, @NotNull Location spawn) {
        if(!(e instanceof Player p)) return true;
        UPlayer uPlayer = UPlayer.getPlayer(p);
        if(uPlayer == null) return true;
        RandomTPData tpData = uPlayer.getRandomTPData();
        if(tpData == null) return true;
        CruxWorld crux = CruxCore.core().worldManager().getWorld(spawn.getWorld().key());
        if(crux == null) return true;

        if(tpData.hasLastTeleported(spawn.getWorld().key())){
            long time = tpData.getLastTeleported(spawn.getWorld().key()).getTime();
            Long worldTime = crux.get(USurviveComponents.WORLD_RANDOM_TELEPORT_TIME);
            if(worldTime != null && time == worldTime) return true;
        }

        Long worldTime = crux.get(USurviveComponents.WORLD_RANDOM_TELEPORT_TIME);
        if(worldTime == null){
            worldTime = System.currentTimeMillis();
            crux.set(USurviveComponents.WORLD_RANDOM_TELEPORT_TIME, worldTime);
        }

        RandomTPData.Pos pos = new RandomTPData.Pos.Simple(CruxLocation.location(spawn), worldTime);
        tpData.setLastTeleported(spawn.getWorld().key(), pos);
        return true;
    }

    @Override
    public void load(@NotNull EntityTeleportHolder entityTeleportHolder, @NotNull Entity entity) {

    }

    @Override
    public void unload(@NotNull EntityTeleportHolder entityTeleportHolder) {

    }
}
