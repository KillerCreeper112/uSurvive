package killercreepr.usurvive.core.guide.progression;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.math.BlockPos;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.core.entity.memory.AdvancementHolder;
import killercreepr.cruxadvancements.core.registries.AdvancementRegistries;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.advancement.objective.GuideDiscoverMeteorObjective;
import killercreepr.usurvive.core.data.DeathGuide;
import killercreepr.usurvive.core.guide.GuideAdvances;
import killercreepr.usurvive.core.guide.GuideHolder;
import killercreepr.usurvive.core.guide.GuideUtil;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.util.AdvanceUtil;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FirstGuideProgression implements GuideProgression{
    public static final Key KEY = Crux.key("first");

    public static class Loader implements FileObjectHandler<FirstGuideProgression>{
        @Override
        public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull FirstGuideProgression object) {
            var r = ctx.getRegistry();
            FileObject o = new FileObject();
            o.add("fallen_meteor_location", r.serializeToFile(object.fallenMeteorLocation));
            o.add("fallen_meteor_world_created_at", r.serializeToFile(object.fallenMeteorWorldCreatedAt));
            o.add("fallen_meteor_world", r.serializeToFile(object.fallenMeteorWorld));
            return o;
        }

        @Nullable
        @Override
        public FirstGuideProgression deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
            var r = ctx.getRegistry();
            FileObject o = (FileObject) e;
            FirstGuideProgression progression = new FirstGuideProgression();
            progression.fallenMeteorLocation = r.deserializeFromFile(BlockPos.class, o.get("fallen_meteor_location"));
            progression.fallenMeteorWorldCreatedAt = r.deserializeFromFileOrDefault(Long.class, o.get("fallen_meteor_world_created_at"), 0L);
            progression.fallenMeteorWorld = r.deserializeFromFile(Key.class, o.get("fallen_meteor_world"));
            return progression;
        }
    }

    @Override
    public void onComplete(Player p, GuideHolder holder) {
        holder.setCurrentProgression(new SecondGuideProgression());
    }

    @Override
    public void onStart(Player p, GuideHolder holder) {
    }

    @Override
    public void onTick(Player p) {
        int every = 30;//cfg.DEATH_GUIDE_TICK_EVERY().value().intValue();
        if(every==0) return;
        if(Crux.getCurrentTick() % every != 0) return;
        tickDistance(p);
        tickStructureGuide(p);
    }

    protected Key fallenMeteorWorld;
    protected BlockPos fallenMeteorLocation;
    protected long fallenMeteorWorldCreatedAt;

    public BlockPos getFallenMeteorLocation() {
        return fallenMeteorLocation;
    }

    public void setFallenMeteorLocation(Location fallenMeteorLocation) {
        if(fallenMeteorLocation != null){
            this.fallenMeteorLocation = BlockPos.at(
                fallenMeteorLocation.getBlockX(), fallenMeteorLocation.getBlockY(), fallenMeteorLocation.getBlockZ()
            );
            this.fallenMeteorWorld = fallenMeteorLocation.getWorld().key();

            CruxWorld crux = CruxCore.core().worldManager().getWorld(fallenMeteorWorld.key());
            if(crux != null){
                fallenMeteorWorldCreatedAt = crux.getOrDefault(CruxWorldsComponents.WORLD_CREATED_AT_TIME, 0L);
            }else fallenMeteorWorldCreatedAt = 0L;
            return;
        }
        this.fallenMeteorLocation = null;
        this.fallenMeteorWorld = null;
        this.fallenMeteorWorldCreatedAt = 0L;
    }

    public void setFallenMeteorLocation(Key world, BlockPos fallenMeteorLocation, long fallenMeteorWorldCreatedAt) {
        this.fallenMeteorLocation = fallenMeteorLocation;
        this.fallenMeteorWorld = world;
        this.fallenMeteorWorldCreatedAt = fallenMeteorWorldCreatedAt;
    }

    public boolean compareCreatedTime(World world){
        long x;
        CruxWorld crux = CruxCore.core().worldManager().getWorld(world.key());
        if(crux != null){
            x = crux.getOrDefault(CruxWorldsComponents.WORLD_CREATED_AT_TIME, 0L);
        }else x = 0L;
        return x == fallenMeteorWorldCreatedAt;
    }

    protected double lastDistanceSquared;
    public void tickDistance(Player p){
        if(fallenMeteorLocation == null) return;
        if(stopTickDistance) return;
        World world = p.getServer().getWorld(fallenMeteorWorld);
        if(world == null || !compareCreatedTime(world)){
            if(!AdvanceUtil.hasAnyAdvancement(p, GuideAdvances.DISCOVER_GUIDE_METEOR, GuideAdvances.GUIDE_SHARD_CLAIM)){
                setFallenMeteorLocation(null);

                Lang.GUIDE_WORLD_RESET.use(p);
                CruxAdvancementManager manager = AdvancementRegistries.ADVANCEMENT_MANAGERS.get(USurvivePlugin.inst().key("main"));
                if(manager == null) return;

                var advancement = manager.getAdvancement(GuideAdvances.ENTER_RESOURCE_WORLD);
                if(advancement == null) return;
                Crux.scheduler().runTaskMain(() ->{
                    manager.revokeAdvancement(p, advancement);
                });
            }
            return;
        }

        if(!p.getWorld().key().equals(fallenMeteorWorld)) return;
        CruxAdvancementManager manager = AdvancementRegistries.ADVANCEMENT_MANAGERS.get(USurvivePlugin.inst().key("main"));
        if(manager == null) return;
        var advancement = manager.getAdvancement(GuideAdvances.DISCOVER_GUIDE_METEOR);
        if(advancement == null) return;
        if(advancement.isGranted(p)) return;

        lastDistanceSquared = fallenMeteorLocation.distanceSquared(CruxPosition.block(p.getLocation()));
        if(lastDistanceSquared > (12D*12D)) return;

        AdvancementHolder holder = holder(p);
        if(holder!= null) {
            stopTickDistance = true;
            Crux.scheduler().runTaskMain(() ->{
                holder.getAdvancementTracker().apply(GuideDiscoverMeteorObjective.class, (man, ad, objective) -> {
                    objective.trigger(p.getUniqueId(), man, ad, LootContext.builder().looter(p).looted(fallenMeteorLocation).build());
                });
                if(advancement.isGranted(p)) Lang.GUIDE_STRUCTURE_NEARBY.use(p);
                stopTickDistance = false;
            });
        }
    }
    protected boolean stopTickDistance = false;

    private AdvancementHolder holder(@NotNull Entity p){
        return AdvancementHolder.holderIfLoaded(p);
    }

    protected final double maxParticleShowDistance = 16D*16D;
    public void tickStructureGuide(@NotNull Player p){
        if(fallenMeteorLocation == null) return;
        if(!p.getWorld().key().equals(fallenMeteorWorld)) return;
        if(GuideUtil.hasGuideDisabled(p)) return;
        if(lastDistanceSquared < maxParticleShowDistance) return;
        //Entity guardian = death.getGuardian();
        Location pos =  fallenMeteorLocation.toLocation(p.getWorld());

        var cfg = USurvivePlugin.inst().values();
        //Location startingPoint = CruxLoc.shiftToward(p.getEyeLocation(), pos, 1D);
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
