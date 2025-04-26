package killercreepr.usurvive.core.world.module;

import io.papermc.paper.event.entity.EntityPortalReadyEvent;
import killercreepr.crux.core.data.util.Pair;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
import killercreepr.cruxworlds.core.world.module.SimpleWorldModule;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ResourceWorldModule extends SimpleWorldModule implements Listener {
    protected final Plugin plugin;
    protected final Pair<Key, CruxWorldType> netherPortalWorld;
    protected final Pair<Key, CruxWorldType> endPortalWorld;
    public ResourceWorldModule(@NotNull CruxWorld parent, Plugin plugin, Pair<Key, CruxWorldType> netherPortalWorld, Pair<Key, CruxWorldType> endPortalWorld) {
        super(parent);
        this.plugin = plugin;
        this.netherPortalWorld = netherPortalWorld;
        this.endPortalWorld = endPortalWorld;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onUnload(boolean save) {
        super.onUnload(save);
        HandlerList.unregisterAll(this);
    }

    public Location findNewTo(Location to, Location from, PlayerTeleportEvent.TeleportCause cause){
        switch (cause){
            case NETHER_PORTAL -> {
                if(netherPortalWorld == null) return null;
                CruxWorld crux = CruxCore.core().worldManager().getWorld(netherPortalWorld.getFirst());
                if(crux == null){
                    crux = netherPortalWorld.getSecond().generate(netherPortalWorld.getFirst());
                }
                if(crux == null){
                    plugin.getLogger().warning("Cannot find world " + netherPortalWorld);
                    return null;
                }
                double x, y, z;
                if(crux.toBukkitWorld().getEnvironment() == World.Environment.NETHER){
                    x = from.getX() / 8;
                    y = from.getY() / 8;
                    z = from.getZ() / 8;
                }else if(from.getWorld().getEnvironment() == World.Environment.NETHER){
                    x = from.getX() * 8;
                    y = from.getY() * 8;
                    z = from.getZ() * 8;
                }else{
                    x = from.getX();
                    y = from.getY();
                    z = from.getZ();
                }
                return new Location(crux.toBukkitWorld(), x, y, z, to.getYaw(), to.getPitch());
            }
            case END_PORTAL -> {
                /*if(endPortalWorld == null){
                }
                if(endPortalWorld == null){
                    plugin.getLogger().warning("Cannot find world " + endPortalWorld);
                    return null;
                }
                CruxWorldUtil.getOrLoadWorld(endPortalWorld);
                CruxWorld crux = CruxCore.core().worldManager().getWorld(endPortalWorld);
                if(crux == null) return null;
                return null;*/
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityPortal(EntityPortalEvent event) {
        if(!event.getFrom().getWorld().key().equals(parent.key())) return;
        var cause = switch (event.getPortalType()){
            case ENDER -> PlayerTeleportEvent.TeleportCause.END_PORTAL;
            case NETHER -> PlayerTeleportEvent.TeleportCause.NETHER_PORTAL;
            case END_GATEWAY -> PlayerTeleportEvent.TeleportCause.END_GATEWAY;
            default -> PlayerTeleportEvent.TeleportCause.UNKNOWN;
        };
        Location newTo = findNewTo(event.getTo(), event.getFrom(), cause);
        if(newTo == null) return;
        event.setTo(newTo);
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if(!event.getFrom().getWorld().key().equals(parent.key())) return;
        Location newTo = findNewTo(event.getTo(), event.getFrom(), event.getCause());
        if(newTo == null) return;
        event.setTo(newTo);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityPortalReady(EntityPortalReadyEvent event) {
        if(!event.getEntity().getWorld().key().equals(parent.key())) return;
        switch (event.getPortalType()){
            case NETHER -> {
                CruxWorld crux = CruxCore.core().worldManager().getWorld(netherPortalWorld.getFirst());
                if(crux == null){
                    crux = netherPortalWorld.getSecond().generate(netherPortalWorld.getFirst());
                }
                if(crux == null) return;
                event.setTargetWorld(crux.toBukkitWorld());
            }
        }
    }

}
