package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.data.util.Pair;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxworlds.api.event.CruxWorldPreCreateEvent;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.creator.CruxWorldModuleCreator;
import killercreepr.usurvive.api.entity.sleeper.SlumberSneaker;
import killercreepr.usurvive.api.entity.sleeper.SlumberSpectator;
import killercreepr.usurvive.api.event.EntityDeepSlumberEvent;
import killercreepr.usurvive.api.event.EntityLeaveSlumberEvent;
import killercreepr.usurvive.api.event.EntitySlumberEvent;
import killercreepr.usurvive.api.world.module.SleeperWorldModule;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.entity.memory.SleeperHolder;
import killercreepr.usurvive.core.entity.sleeper.BedSlumberData;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.util.RespawnUtil;
import killercreepr.usurvive.core.world.module.SimpleSleeperWorldModule;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

public class BedListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;
        Player p = event.getPlayer();
        if(p.isSneaking() || event.getHand() != EquipmentSlot.HAND) return;
        Block b = event.getClickedBlock();
        if(b == null) return;
        if(!(b.getBlockData() instanceof Bed bed)) return;

        SleeperHolder sleeper = EntityMemory.getDataHolder(p, SleeperHolder.class);
        if(sleeper==null) return;

        if(!canSleepInBed(b)){
            Lang.BED_CANNOT_USE_HERE.use(p);
            return;
        }

        event.setCancelled(true);
        BedSlumberData slumberData = new BedSlumberData(p, b.getLocation());
        Pair<Block, Block> bedPair = RespawnUtil.getFullBed(b, bed);
        if(bedPair == null || !slumberData.canSleep()){
            Lang.BED_OBSTRUCTED.use(p);
            return;
        }
        if(isBedOccupied(b, bed)){
            Lang.BED_OCCUPIED.use(p);
            return;
        }
        if(!isBedCloseEnough(bedPair, p.getLocation())){
            Lang.BED_TOO_FAR.use(p);
            return;
        }
        if (p.getGameMode() != GameMode.CREATIVE && areMonstersNearby(b)) {
            Lang.BED_MONSTERS_NEARBY.use(p);
            return;
        }
        sleeper.sleep(slumberData);
    }

    public boolean isBedCloseEnough(Pair<Block, Block> bedPair, Location pos){
        return isReachableBed(bedPair.getFirst(), pos) || isReachableBed(bedPair.getSecond(), pos);
    }

    private boolean isReachableBed(Block bed, Location pos) {
        Location vec = bed.getLocation().toCenterLocation().subtract(0, .5, 0);
        return Math.abs(pos.getX() - vec.x()) <= (double)3.0F
            && Math.abs(pos.getY() - vec.y()) <= (double)2.0F &&
            Math.abs(pos.getZ() - vec.z()) <= (double)3.0F;
    }

    public boolean canSleepInBed(Block b){
        World world = b.getWorld();
        return world.isNatural() || world.isBedWorks();
    }

    public boolean isBedOccupied(Block b, Bed bed){
        return bed.isOccupied();
    }

    public boolean areMonstersNearby(Block b){
        return !b.getWorld().getNearbyEntities(b.getLocation().toCenterLocation(), 8D, 5D, 8D, e ->{
            return CruxMob.isInCategory(e, MobCategory.MONSTER);
        }).isEmpty();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySlumber(EntitySlumberEvent event) {
        Entity e = event.getEntity();
        CruxWorld crux = CruxCore.core().worldManager().getWorld(e.getWorld().getUID());
        if(crux == null) return;
        SleeperWorldModule sleep = crux.getModule(SleeperWorldModule.class);
        if(sleep == null) return;
        sleep.addSleeper(e.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeepSlumber(EntityDeepSlumberEvent event) {
        Entity e = event.getEntity();
        CruxWorld crux = CruxCore.core().worldManager().getWorld(e.getWorld().getUID());
        if(crux == null) return;
        SleeperWorldModule sleep = crux.getModule(SleeperWorldModule.class);
        if(sleep == null) return;
        sleep.addDeepSleeper(e.getUniqueId());
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityLeaveSlumber(EntityLeaveSlumberEvent event) {
        Entity e = event.getEntity();
        CruxWorld crux = CruxCore.core().worldManager().getWorld(e.getWorld().getUID());
        if(crux == null) return;
        SleeperWorldModule sleep = crux.getModule(SleeperWorldModule.class);
        if(sleep == null) return;
        sleep.removeSleeper(e.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onEntityTeleport(EntityTeleportEvent event) {
        Entity e = event.getEntity();
        SleeperHolder sleeper = EntityMemory.getDataHolder(e, SleeperHolder.class);
        if(sleeper==null) return;
        if(!sleeper.isSleeping()) return;
        sleeper.sleep(null);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();
        SleeperHolder sleeper = EntityMemory.getDataHolder(e, SleeperHolder.class);
        if(sleeper==null) return;
        if(!sleeper.isSleeping()) return;
        if(event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION){
            return;
        }
        sleeper.sleep(null);
    }

    @EventHandler(ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) return;
        Entity e = event.getPlayer();
        SleeperHolder sleeper = EntityMemory.getDataHolder(e, SleeperHolder.class);
        if(sleeper==null) return;
        if(!sleeper.isSleeping()) return;
        sleeper.sleep(null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Entity e = event.getPlayer();
        SleeperHolder sleeper = EntityMemory.getDataHolder(e, SleeperHolder.class);
        if(sleeper==null) return;
        if(!sleeper.isSleeping()) return;
        sleeper.sleep(null);

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player p = event.getPlayer();
        SleeperHolder sleeper = EntityMemory.getDataHolder(p, SleeperHolder.class);
        if(sleeper==null) return;
        if(!sleeper.isSleeping()) return;
        if(!(sleeper.getSlumberData() instanceof SlumberSneaker spec)) return;
        spec.onToggleSneak(event);
    }

    @EventHandler(ignoreCancelled = true,priority = EventPriority.HIGH)
    public void onPlayerStopSpectatingEntity(PlayerStopSpectatingEntityEvent event) {
        Player p = event.getPlayer();
        SleeperHolder sleeper = EntityMemory.getDataHolder(p, SleeperHolder.class);
        if(sleeper==null) return;
        if(!sleeper.isSleeping()) return;
        if(!(sleeper.getSlumberData() instanceof SlumberSpectator spec)) return;
        spec.onStopSpectating(event);
    }

    protected final CruxWorldModuleCreator moduleCreator = world -> new SimpleSleeperWorldModule(
        world, () -> USurvivePlugin.inst().values().SLEEPER_WORLD_TIME_SKIP()
    );
    @EventHandler(ignoreCancelled = true)
    public void onCruxWorldPreCreate(CruxWorldPreCreateEvent event) {
        World world = event.getWorld();
        if(world.getEnvironment() == World.Environment.NETHER || world.getEnvironment() == World.Environment.THE_END) return;
        event.getModuleCreators().add(moduleCreator);
    }
}
