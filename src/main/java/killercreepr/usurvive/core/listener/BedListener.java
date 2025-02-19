package killercreepr.usurvive.core.listener;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxworlds.api.event.CruxWorldPreCreateEvent;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.creator.CruxWorldModuleCreator;
import killercreepr.usurvive.api.entity.sleeper.SlumberData;
import killercreepr.usurvive.api.entity.sleeper.SlumberSpectator;
import killercreepr.usurvive.api.event.EntityDeepSlumberEvent;
import killercreepr.usurvive.api.event.EntityLeaveSlumberEvent;
import killercreepr.usurvive.api.event.EntitySlumberEvent;
import killercreepr.usurvive.api.world.module.SleeperWorldModule;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.entity.memory.SleeperHolder;
import killercreepr.usurvive.core.entity.sleeper.BedSlumberData;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.world.module.SimpleSleeperWorldModule;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.BedBlock;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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

        event.setCancelled(true);

        if(!canSleepInBed(b)){
            Lang.BED_CANNOT_USE_HERE.use(p);
            return;
        }

        BedSlumberData slumberData = new BedSlumberData(p, b.getLocation());
        if(!slumberData.canSleep()){
            Lang.BED_OBSTRUCTED.use(p);
            return;
        }
        if(isBedOccupied(b, bed)){
            Lang.BED_OCCUPIED.use(p);
            return;
        }
        if (areMonstersNearby(b)) {
            Lang.BED_MONSTERS_NEARBY.use(p);
            return;
        }
        sleeper.sleep(slumberData);
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

    @EventHandler(ignoreCancelled = true)
    public void onEntityTeleport(EntityTeleportEvent event) {
        Entity e = event.getEntity();
        SleeperHolder sleeper = EntityMemory.getDataHolder(e, SleeperHolder.class);
        if(sleeper==null) return;
        if(!sleeper.isSleeping()) return;
        sleeper.sleep(null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
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

    @EventHandler(ignoreCancelled = true)
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
