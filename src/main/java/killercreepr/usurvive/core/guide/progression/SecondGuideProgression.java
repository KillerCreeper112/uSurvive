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
import killercreepr.usurvive.core.data.DeathGuide;
import killercreepr.usurvive.core.guide.GuideHolder;
import killercreepr.usurvive.core.guide.GuideUtil;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SecondGuideProgression implements GuideProgression{
    public static final Key KEY = Crux.key("second");

    public static class Loader implements FileObjectHandler<SecondGuideProgression>{
        @Override
        public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull SecondGuideProgression object) {
            var r = ctx.getRegistry();
            FileObject o = new FileObject();
            return o;
        }

        @Nullable
        @Override
        public SecondGuideProgression deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
            var r = ctx.getRegistry();
            FileObject o = (FileObject) e;
            SecondGuideProgression progression = new SecondGuideProgression();
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
        holder.setCurrentProgression(new ThirdGuideProgression());
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

        var cfg = USurvivePlugin.inst().values();
        new DeathGuide(p, p.getLocation().add(0, p.getHeight()/2, 0), pos,
            cfg.DEATH_GUIDE_PARTICLE_AMOUNT().value().intValue(),
            cfg.DEATH_GUIDE_PARTICLE_SHIFT_START(),
            cfg.DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER(),
            cfg.DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL(),
            cfg.DEATH_GUIDE_PARTICLE_OFFSET_SIDE(),
            cfg.DEATH_GUIDE_PARTICLE_SHIFT(),
            Particle.FLAME
        ).setStarted();
    }

    @NotNull
    @Override
    public Key key() {
        return KEY;
    }
}
