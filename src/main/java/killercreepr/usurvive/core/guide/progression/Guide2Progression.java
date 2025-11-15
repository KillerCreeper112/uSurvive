package killercreepr.usurvive.core.guide.progression;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.cruxcore.CruxCore;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.block.ActiveOrbitConduit;
import killercreepr.usurvive.core.guide.GuideHolder;
import killercreepr.usurvive.core.guide.GuideUtil;
import killercreepr.usurvive.core.guide.particle.TrailRenderer;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.pathfinding.GuidePathfinder;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Guide2Progression extends GenericGuideProgression{
    public static final Key KEY = Crux.key("2");

    public static class Loader implements FileObjectHandler<Guide2Progression>{
        @Override
        public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull Guide2Progression object) {
            var r = ctx.getRegistry();
            FileObject o = new FileObject();
            return o;
        }

        @Nullable
        @Override
        public Guide2Progression deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
            var r = ctx.getRegistry();
            FileObject o = (FileObject) e;
            Guide2Progression progression = new Guide2Progression();
            return progression;
        }
    }

    long lastCheckedConduit;
    public void findAndSetConduit(Block center){
        lastCheckedConduit = System.currentTimeMillis();
        conduit = findConduit(center);
    }

    public Block findConduit(Block center){
        return findConduit(center, 48);
    }

    public Block findConduit(Block center, int range){
        for(int x = -range; x <= range; x++){
            for(int z = -range; z <= range; z++){
                for(int y = -range; y <= range; y++){
                    Block b = center.getRelative(x, y, z);
                    var block = CruxCore.core().cruxBlocks().getActiveBlock(b);
                    if(block == null) continue;
                    if(!(block instanceof ActiveOrbitConduit)) continue;
                    return b;
                }
            }
        }
        return null;
    }

    @Override
    public void onComplete(Player p, GuideHolder holder) {
        holder.setCurrentProgression(new Guide3Progression());
    }

    @Override
    public void onStart(Player p, GuideHolder holder) {
        if(GuideUtil.hasGuideDisabled(p)) return;
        Crux.scheduler().runTaskLater(() ->{
            Lang.GUIDE_SECOND_START.use(p);
        }, 2L);
    }

    @Override
    public void onTick(Player p) {
        super.onTick(p);
        int every = 30;
        if(every==0) return;
        if(Crux.getCurrentTick() % every != 0) return;
        tickStructureGuide(p);
    }

    public boolean isAtSpawn(World world){
        String spawn = USurvivePlugin.inst().values().SPAWN_WORLD().value();
        return world.getName().equals(spawn);
    }

    protected Block conduit;
    public void tickStructureGuide(@NotNull Player p){
        if(GuideUtil.hasGuideDisabled(p)) return;
        if(conduit == null){
            if(!isAtSpawn(p.getWorld())) return;
            if(CruxMath.hasOccurredWithin(lastCheckedConduit, 80L)) return;
            findAndSetConduit(p.getLocation().getBlock());
            return;
        }
        if(!conduit.getWorld().equals(p.getWorld())) return;
        Location pos =  conduit.getLocation();

        List<Location> path = new GuidePathfinder(p).findPath(p.getLocation(), pos, 0.5D,
            cfg().GUIDE_1_MAX_PARTICLE_POINTS.value().value().intValue());
        TrailRenderer.renderTrail(p, path);
    }

    @NotNull
    @Override
    public Key key() {
        return KEY;
    }
}
