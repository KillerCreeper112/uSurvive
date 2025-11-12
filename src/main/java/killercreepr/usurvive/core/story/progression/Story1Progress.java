package killercreepr.usurvive.core.story.progression;

import killercreepr.crux.api.data.WorldPosition;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.math.BlockPos;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.core.entity.memory.AdvancementHolder;
import killercreepr.cruxadvancements.core.registries.AdvancementRegistries;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalNode;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalPath;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import killercreepr.usurvive.api.entity.mob.goal.path.GuidePathGoalNode;
import killercreepr.usurvive.api.guide.GuidePoint;
import killercreepr.usurvive.api.story.FileStoryProgressionLoader;
import killercreepr.usurvive.api.story.StoryHolder;
import killercreepr.usurvive.api.story.StoryProgressData;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.advancement.objective.GuideDiscoverMeteorObjective;
import killercreepr.usurvive.core.data.DeathGuide;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.entity.mob.goals.guide.GuidePathGoal;
import killercreepr.usurvive.core.guide.GuideAdvances;
import killercreepr.usurvive.core.guide.GuideHolder;
import killercreepr.usurvive.core.guide.GuideUtil;
import killercreepr.usurvive.core.guide.particle.TrailRenderer;
import killercreepr.usurvive.core.guide.progression.Guide2Progression;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.pathfinding.GuidePathfinder;
import killercreepr.usurvive.core.story.progression.data.GenericStoryProgressData;
import killercreepr.usurvive.core.util.AdvanceUtil;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Story1Progress extends StoryHolderStoryProgress implements Listener {
    public static final Key KEY = Crux.key("1");

    public static class Loader implements FileStoryProgressionLoader<Story1Progress, Data>{
        public static final FileObjectHandler<Story1Progress> tempHandler = new AutoFileHandler<>(Story1Progress.class);
        public static final FileObjectHandler<Data> permHandler = new AutoFileHandler<>(Data.class);

        @Override
        public FileObjectHandler<Story1Progress> tempDataHandler() {
            return tempHandler;
        }

        @Override
        public FileObjectHandler<Data> permanentDataHandler() {
            return permHandler;
        }
    }

    public static class Data extends GenericStoryProgressData {
        public boolean pushedNPCInPortal;

        @NotNull
        @Override
        public Key key() {
            return KEY;
        }
    }

    public transient Data data = new Data();
    public Key fallenMeteorWorld;
    public BlockPos fallenMeteorLocation;
    public long fallenMeteorWorldCreatedAt;
    public boolean enteredResourceOverworld;

    @Override
    public StoryProgressData getProgress() {
        return data;
    }

    @Override
    public void setProgress(StoryProgressData data) {
        if(data == null) this.data = new Data();
        else this.data = (Data) data;
    }

    @Override
    public @NotNull Key key() {
        return KEY;
    }

    @Override
    public void onComplete(Player p, StoryHolder holder) {
        //todo holder.setCurrentProgression(new Guide2Progression());
    }

    @Override
    public void onMemoryUnload(Player p, StoryHolder holder) {
        super.onMemoryUnload(p, holder);
        if(npc != null){
            npc.remove();
            npc = null;
            npcGoal = null;
        }
    }

    @Override
    public void onEnd(Player p, StoryHolder holder) {
        super.onEnd(p, holder);
        if(npc != null){
            npc.remove();
            npc = null;
            npcGoal = null;
        }
    }

    protected Mob npc;
    protected GuidePathGoal npcGoal;

    public void setupNPC(Player p){
        npc = (Mob) USurviveMobs.GUIDE_1_NPC.spawn(cfg().GUIDE_1_POINTS.value().getFirst().pos().toLocation());
        npc.setVisibleByDefault(false);
        p.showEntity(Crux.getMainPlugin(), npc);
        npcGoal = CruxGoalUtil.getGoal(npc, GuidePathGoal.class);
        List<GoalNode> nodes = new ArrayList<>();

        cfg().GUIDE_1_POINTS.valueOr(List.of()).forEach(pos ->{
            nodes.add(GuidePathGoalNode.guidePathGoalNode(
                GoalNode.distanceGoalNode(pos.pos().toLocation(), 1.5D),
                pos
            ));
        });

        npcGoal.setUser(p);
        //goal.setDialog(cfg().GUIDE_1_DIALOG().value());
        npcGoal.setPath(GoalPath.goalPath(nodes));
    }

    @Override
    public void onLoaded(Player p, StoryHolder holder) {
        super.onLoaded(p, holder);
        if(hasEnteredResourceOverworld()) return;
        if(npc != null) return;
        setupNPC(p);
    }

    protected Player p;
    @Override
    public void onStart(Player p, StoryHolder holder) {
        this.p = p;
        super.onStart(p, holder);
        if(hasEnteredResourceOverworld() || !p.getWorld().key().equals(cfg().GUIDE_1_POINTS.value().getFirst().pos().worldKey())) return;
        WorldPosition spawn = cfg().GUIDE_1_SPAWN.value();
        p.teleport(spawn.toLocation());
    }

    @Override
    public void onTick() {
        super.onTick();
        int every = 30;//cfg.DEATH_GUIDE_TICK_EVERY().value().intValue();
        if(every==0) return;
        if(Crux.getCurrentTick() % every != 0) return;

        if(!hasEnteredResourceOverworld() && p.getWorld().key().equals(cfg().GUIDE_1_POINTS.value().getFirst().pos().worldKey())){
            if((npcGoal == null || !npcGoal.isSpeaking() || npcGoal.isMoving())){
                GuidePoint nearest = CruxCollection.getLast(cfg().GUIDE_1_POINTS.value());
                if(nearest != null){
                    List<Location> path = new GuidePathfinder(p).findPath(p.getLocation(), nearest.pos().toLocation(), 0.5D,
                        cfg().GUIDE_1_MAX_PARTICLE_POINTS.value().value().intValue());
                    TrailRenderer.renderTrail(p, path);
                }
            }
        }

        tickDistance(p);
        tickStructureGuide(p);
    }

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
        if(lastDistanceSquared > (30D*30D)) return;

        AdvancementHolder holder = holder(p);
        if(holder!= null) {
            stopTickDistance = true;
            Crux.scheduler().runTaskMain(() ->{
                holder.getAdvancementTracker().apply(GuideDiscoverMeteorObjective.class, (man, ad, objective) -> {
                    objective.trigger(p.getUniqueId(), man, ad, LootContext.builder().looter(p).looted(fallenMeteorLocation).build());
                });
                if(advancement.isGranted(p)){
                    dialogPlayer.queueDialog(cfg().GUIDE_1_STRUCTURE_NEARBY.value());
                    //Lang.GUIDE_STRUCTURE_NEARBY.use(p);
                }
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
        new DeathGuide(p, p.getLocation().add(0, p.getHeight()*.65, 0), pos,
            cfg.DEATH_GUIDE_PARTICLE_AMOUNT().value().intValue(),
            cfg.DEATH_GUIDE_PARTICLE_SHIFT_START(),
            cfg.DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER(),
            cfg.DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL(),
            cfg.DEATH_GUIDE_PARTICLE_OFFSET_SIDE(),
            cfg.DEATH_GUIDE_PARTICLE_SHIFT(),
            Particle.FLAME
        ).setStarted();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        if(!p.equals(this.p)) return;
        if(npc != null){
            npc.remove();
            npc = null;
            npcGoal = null;
        }
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        if(!p.equals(this.p)) return;
        if(p.hasPermission("usurvive.guide.restrictions.bypass")) return;
        if(hasEnteredResourceOverworld()) return;
        event.setCancelled(true);
        Lang.GUIDE_1_CANNOT_USE_COMMANDS.use(p);
    }

    public boolean hasEnteredResourceOverworld() {
        return enteredResourceOverworld;
    }

    public void onEnteredResourceOverworld(){
        if(p == null) return;
        dialogPlayer.queueDialog(cfg().GUIDE_1_ENTER_RESOURCE_WORLD_DIALOG.value());
    }

    public void setEnteredResourceOverworld(boolean enteredResourceOverworld) {
        if(!this.enteredResourceOverworld && enteredResourceOverworld){
            onEnteredResourceOverworld();
        }
        this.enteredResourceOverworld = enteredResourceOverworld;
    }
}
