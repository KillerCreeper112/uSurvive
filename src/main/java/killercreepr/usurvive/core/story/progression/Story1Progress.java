package killercreepr.usurvive.core.story.progression;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.data.WorldPosition;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.math.BlockPos;
import killercreepr.crux.core.persistence.CruxPersistence;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxadvancements.core.entity.memory.AdvancementHolder;
import killercreepr.cruxblocks.api.event.CruxBlockBreakEvent;
import killercreepr.cruxblocks.core.block.component.standard.BlockEntitySpawner;
import killercreepr.cruxblocks.core.block.component.standard.EntitySpawnerComponent;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalNode;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalPath;
import killercreepr.cruxstructures.api.structure.generation.StructureGenerator;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import killercreepr.usurvive.api.entity.mob.goal.path.GuidePathGoalNode;
import killercreepr.usurvive.api.guide.GuidePoint;
import killercreepr.usurvive.api.story.*;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.advancement.objective.GuideDiscoverMeteorObjective;
import killercreepr.usurvive.core.block.USurviveBlocks;
import killercreepr.usurvive.core.config.story.Story1Config;
import killercreepr.usurvive.core.data.DeathGuide;
import killercreepr.usurvive.core.dialog.WrappedDialogContent;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.entity.mob.goals.guide.GuidePathGoal;
import killercreepr.usurvive.core.guide.GuideStructurePlacer;
import killercreepr.usurvive.core.guide.particle.TrailRenderer;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.pathfinding.GuidePathfinder;
import killercreepr.usurvive.core.story.SimpleStoryState;
import killercreepr.usurvive.core.story.progression.data.GenericStoryProgressData;
import killercreepr.usurvive.core.world.USurviveWorldTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class Story1Progress extends StoryHolderStoryProgress implements Listener {
    public enum NumFlags{
        COUNT_ENEMY_SPAWN,
        COUNT_ENEMY_KILL,
        ;
        private final StoryFlag flag;
        NumFlags(StoryFlag flag) {
            this.flag = flag;
        }
        NumFlags() {
            this.flag = StoryFlag.f(toString().toLowerCase());
        }
        NumFlags(Function<StoryState, Boolean> isComplete) {
            this.flag = StoryFlag.f(toString().toLowerCase(), isComplete);
        }

        public StoryFlag flag(){
            return flag;
        }
    }
    public enum Flags{
        ENTERED_RESOURCE_OVERWORLD,
        ENEMIES_SPAWNED,
        ORBIT_MINED,
        DEFEATED_DIALOG_DONE,

        FOUND_STRUCTURE,
        ENEMIES_KILLED(state -> state.getFlagValue(NumFlags.COUNT_ENEMY_KILL.flag()) > 0 && state.getFlagValue(NumFlags.COUNT_ENEMY_KILL.flag()) >= state.getFlagValue(NumFlags.COUNT_ENEMY_SPAWN.flag())),
        ;
        private final StoryFlag flag;
        Flags(StoryFlag flag) {
            this.flag = flag;
        }
        Flags() {
            this.flag = StoryFlag.f(toString().toLowerCase());
        }
        Flags(Function<StoryState, Boolean> isComplete) {
            this.flag = StoryFlag.f(toString().toLowerCase(), isComplete);
        }

        public StoryFlag flag(){
            return flag;
        }
    }
    public enum DataFlags{
        PUSHED_NPC_IN_PORTAL,
        PUNCHED_NPC,
        ;
        private final StoryFlag flag;
        DataFlags(StoryFlag flag) {
            this.flag = flag;
        }
        DataFlags() {
            this.flag = StoryFlag.f(toString().toLowerCase());
        }
        DataFlags(Function<StoryState, Boolean> isComplete) {
            this.flag = StoryFlag.f(toString().toLowerCase(), isComplete);
        }

        public StoryFlag flag(){
            return flag;
        }
    }
    protected transient List<StoryFlag> cacheRequiredFlags;
    @Override
    public List<StoryFlag> getRequiredFlags() {
        if(cacheRequiredFlags != null) return cacheRequiredFlags;
        cacheRequiredFlags = new ArrayList<>();
        for (Flags value : Flags.values()) {
            cacheRequiredFlags.add(value.flag());
        }
        return cacheRequiredFlags;
    }

    public transient final List<StoryStep> steps = List.of(
        new StoryStep() {
            @Override
            public boolean canStart(StoryState state) {
                return !hasEnteredResourceOverworld();
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.ENTERED_RESOURCE_OVERWORLD.flag());
            }

            @Override
            public void onStart(Player p) {
                if(!canSetup()) return;
                setup();
            }
        },

        new StoryStep() {
            @Override
            public boolean canStart(StoryState state) {
                return state.hasFlag(Flags.ENTERED_RESOURCE_OVERWORLD.flag());
            }

            @Override
            public boolean isComplete(StoryState state) {
                return fallenMeteorLocation != null;
            }

            @Override
            public DialogContent getDialog() {
                return cfg().ENTER_RESOURCE_WORLD_DIALOG.value();
            }

            @Override
            public void onStart(Player p) {
                if(isComplete(state)) return;
                if(isResourceOverworld(p.getWorld())){
                    onEnterResourceOverworld(p.getWorld());
                }
            }
        },

        new StoryStep() {
            @Override
            public boolean canStart(StoryState state) {
                return fallenMeteorLocation != null;
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.FOUND_STRUCTURE.flag());
            }

            @Override
            public DialogContent getDialog() {
                return cfg().STRUCTURE_LOCATED_DIALOG.value();
            }
        },

        new StoryStep() {
            @Override
            public boolean canStart(StoryState state) {
                return state.hasFlag(Flags.FOUND_STRUCTURE.flag());
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.ENEMIES_SPAWNED.flag());
            }

            @Override
            public DialogContent getDialog() {
                var content = cfg().STRUCTURE_NEARBY_DIALOG.value();
                if(content != null){
                    content = new WrappedDialogContent(content){
                        @Override
                        public void onSendInfo(DataExchange info) {
                            String action = info.get("initiate", String.class);
                            if(action == null) return;
                            switch (action.toLowerCase()){
                                case "spawn_bounders" ->{
                                    var typed = CruxRegistries.TYPED_DATA_COMPONENTS.get(Crux.key(info.get("spawner_component", String.class)));
                                    if(!(typed.getValue() instanceof EntitySpawnerComponent spawner)){
                                        throw new IllegalStateException("Spawn bounders initiate not have spawner_component of EntitySpawnerComponent!  " + typed.getType() + "   /   " + typed.getValue());
                                    }
                                    state.grantFlag(Flags.ENEMIES_SPAWNED.flag());
                                    //Block b = fallenMeteorLocation.getBlock(world);
                                    Crux.scheduler().runTaskMain(() ->{
                                        new BlockEntitySpawner(
                                            p.getLocation().getBlock(),
                                            spawner, spawner.spawner, e ->{
                                            if(spawner.persistEntities){
                                                e.setPersistent(true);
                                                if(e instanceof Mob mob) mob.setRemoveWhenFarAway(false);
                                            }
                                        }
                                        ).navigateSpawner().whenComplete((spawned, throwable) ->{
                                            if(throwable != null) throwable.printStackTrace();
                                            if(spawned == null || spawned.isEmpty()){
                                                return;
                                            }
                                            completeStep(() ->{
                                                state.grantFlag(Flags.ENEMIES_SPAWNED.flag());
                                                state.setFlagValue(NumFlags.COUNT_ENEMY_SPAWN.flag(), spawned.size());
                                                spawned.forEach(e ->{
                                                    setStoryMob(e);
                                                });
                                            });
                                        });
                                    });
                                }
                            }

                        }
                    };
                }
                return content;
            }
        },

        new StoryStep() {
            @Override
            public boolean canStart(StoryState state) {
                return state.hasFlag(Flags.ENEMIES_SPAWNED.flag());
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.ENEMIES_KILLED.flag());
            }

            @Override
            public DialogContent getDialog() {
                return cfg().BOUNDERS_INVADE_SPAWNED_DIALOG.value();
            }
        },

        new StoryStep() {
            @Override
            public boolean canStart(StoryState state) {
                return state.hasFlag(Flags.ENEMIES_KILLED.flag());
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.DEFEATED_DIALOG_DONE.flag());
            }

            @Override
            public DialogContent getDialog() {
                return new WrappedDialogContent(cfg().BOUNDERS_DEFEATED_DIALOG.value()){
                    @Override
                    public void onDialogEnd(DialogPlayer player) {
                        super.onDialogEnd(player);
                        completeStep(() ->{
                            state.grantFlag(Flags.DEFEATED_DIALOG_DONE.flag());
                        });
                    }
                };
            }
        },

        new StoryStep() {
            @Override
            public boolean canStart(StoryState state) {
                return state.hasFlag(Flags.DEFEATED_DIALOG_DONE.flag());
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.ORBIT_MINED.flag());
            }
        }
    );

    @Override
    public List<StoryStep> getSteps() {
        return steps;
    }

    @Override
    public StoryState state() {
        return state;
    }

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
        public StoryState state = new SimpleStoryState();

        @NotNull
        @Override
        public Key key() {
            return KEY;
        }
    }

    public transient Data data = new Data();
    public StoryState state = new SimpleStoryState();
    public Key fallenMeteorWorld;
    public BlockPos fallenMeteorLocation;
    public long fallenMeteorWorldCreatedAt;

    protected transient Mob npc;
    protected transient GuidePathGoal npcGoal;
    protected transient double lastDistanceSquared;
    protected transient boolean stopTickDistance = false;
    protected transient final double maxParticleShowDistance = 16D*16D;

    @Override
    public Story1Config cfg() {
        return USurvivePlugin.inst().cfgContainer().STORY_1;
    }

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

    public void setupNPC(Player p){
        npc = (Mob) USurviveMobs.GUIDE_1_NPC.spawn(cfg().NPC_POINTS_1.value().getFirst().pos().toLocation());
        npc.setVisibleByDefault(false);
        p.showEntity(Crux.getMainPlugin(), npc);
        npcGoal = CruxGoalUtil.getGoal(npc, GuidePathGoal.class);
        List<GoalNode> nodes = new ArrayList<>();

        cfg().NPC_POINTS_1.valueOr(List.of()).forEach(pos ->{
            nodes.add(GuidePathGoalNode.guidePathGoalNode(
                GoalNode.distanceGoalNode(pos.pos().toLocation(), 1.5D),
                pos
            ));
        });

        npcGoal.setUser(p);
        npcGoal.setPath(GoalPath.goalPath(nodes));
    }

    public boolean canSetup(){
        return !hasEnteredResourceOverworld() && p.getWorld().key().equals(cfg().NPC_POINTS_1.value().getFirst().pos().worldKey());
    }

    public void setupPlayer(){
        WorldPosition spawn = cfg().NPC_SPAWN_1.value();
        var loc = spawn.toLocation();
        p.teleport(loc);

        hideEntities(loc.getWorld());
    }

    public void setupNPC(){
        if(npc != null) return;
        setupNPC(p);
    }

    public void setup(){
        setupPlayer();
        setupNPC();
    }

    public void showEntities(World world){
        for (Mob mob : world.getEntitiesByClass(Mob.class)) {
            //mythicmobstype npc_guide_1
            if(!"npc_guide_1".equalsIgnoreCase(CruxTag.get(mob, "mythicmobs:type", PersistentDataType.STRING, null))) continue;
            if(p.canSee(mob)) continue;
            p.showEntity(Crux.getMainPlugin(), mob);
        }
    }

    public void hideEntities(World world){
        for (Mob mob : world.getEntitiesByClass(Mob.class)) {
            //mythicmobstype npc_guide_1
            if(!"npc_guide_1".equalsIgnoreCase(CruxTag.get(mob, "mythicmobs:type", PersistentDataType.STRING, null))) continue;
            p.hideEntity(Crux.getMainPlugin(), mob);
        }
    }

    @Override
    public void onLoaded(Player p, StoryHolder holder) {
        super.onLoaded(p, holder);
        restorePlayer(p);
        /*if(!canSetup()) return;
        setupNPC();*/
    }

    @Override
    public void onStart(Player p, StoryHolder holder) {
        super.onStart(p, holder);
        /*if(!canSetup()) return;
        setupPlayer();*/
    }

    @Override
    public void onTick() {
        super.onTick();
        int every = 30;//cfg.DEATH_GUIDE_TICK_EVERY().value().intValue();
        //if(every==0) return;
        if(Crux.getCurrentTick() % every != 0) return;

        if(!hasEnteredResourceOverworld() && p.getWorld().key().equals(cfg().NPC_POINTS_1.value().getFirst().pos().worldKey())){
            if((npcGoal == null || !npcGoal.isSpeaking() || npcGoal.isMoving())){
                GuidePoint nearest = CruxCollection.getLast(cfg().NPC_POINTS_1.value());
                if(nearest != null){
                    List<Location> path = new GuidePathfinder(p).findPath(p.getLocation(), nearest.pos().toLocation(), 0.5D,
                        cfg().MAX_PARTICLE_POINTS.value().value().intValue());
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

    public void tickDistance(Player p){
        if(fallenMeteorLocation == null) return;
        if(stopTickDistance) return;
        World world = p.getServer().getWorld(fallenMeteorWorld);
        if(world == null || !compareCreatedTime(world)){
            if(!state.hasFlag(Flags.FOUND_STRUCTURE.flag())){
                setFallenMeteorLocation(null);
                Lang.GUIDE_WORLD_RESET.use(p);
            }
            return;
        }

        if(!p.getWorld().key().equals(fallenMeteorWorld)) return;

        /*if(state.hasFlag(Flags.FOUND_STRUCTURE.flag())){
            if(!state.hasFlag(Flags.ENEMIES_SPAWNED.flag()) && dialogListener().dialogPlayer().isEmpty()){
                state.setFlag(Flags.FOUND_STRUCTURE.flag(), false);
            }
            return;
        }*/

        lastDistanceSquared = fallenMeteorLocation.distanceSquared(CruxPosition.block(p.getLocation()));
        if(state.hasFlag(Flags.FOUND_STRUCTURE.flag())) return;
        if(lastDistanceSquared > (30D*30D)) return;

        AdvancementHolder holder = holder(p);
        if(holder!= null) {
            stopTickDistance = true;
            Crux.scheduler().runTaskMain(() ->{
                completeStep(() ->{
                    holder.getAdvancementTracker().apply(GuideDiscoverMeteorObjective.class, (man, ad, objective) -> {
                        objective.trigger(p.getUniqueId(), man, ad, LootContext.builder().looter(p).looted(fallenMeteorLocation).build());
                    });
                    state.grantFlag(Flags.FOUND_STRUCTURE.flag());
                });
                stopTickDistance = false;
            });
        }
    }

    public void setStoryMob(Entity e){
        CruxTag.set(e, "story/player", CruxPersistence.UUID, p.getUniqueId());
    }

    public boolean isStoryMob(Entity e){
        return p.getUniqueId().equals(CruxTag.get(e, "story/player", CruxPersistence.UUID, null));
    }

    private AdvancementHolder holder(@NotNull Entity p){
        return AdvancementHolder.holderIfLoaded(p);
    }

    public void tickStructureGuide(@NotNull Player p){
        if(fallenMeteorLocation == null) return;
        if(!p.getWorld().key().equals(fallenMeteorWorld)) return;
        //if(GuideUtil.hasGuideDisabled(p)) return;
        if(lastDistanceSquared < maxParticleShowDistance) return;
        //Entity guardian = death.getGuardian();
        Location pos =  fallenMeteorLocation.toLocation(p.getWorld());

        var cfg = USurvivePlugin.inst().values();
        //Location startingPoint = CruxLoc.shiftToward(p.getEyeLocation(), pos, 1D);
        new DeathGuide(p, p.getLocation().add(0, p.getHeight()*.7, 0), pos,
            cfg.DEATH_GUIDE_PARTICLE_AMOUNT().value().intValue(),
            cfg.DEATH_GUIDE_PARTICLE_SHIFT_START(),
            cfg.DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER(),
            cfg.DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL(),
            cfg.DEATH_GUIDE_PARTICLE_OFFSET_SIDE(),
            cfg.DEATH_GUIDE_PARTICLE_SHIFT(),
            Particle.FLAME
        ).setStarted();
    }

    @Override
    public void onComplete(Player p, StoryHolder holder) {
        super.onComplete(p, holder);
        holder.addProgression(new Story2Progress());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCruxBlockBreak(CruxBlockBreakEvent event) {
        if(!isPlayer(event.getContext().getMiner())) return;
        if(!event.getBlock().key().equals(USurviveBlocks.ORBIT_ORE.key())) return;
        completeStep(() ->{
            state.grantFlag(Flags.ORBIT_MINED.flag());
        });
    }

    public void onEnterResourceOverworld(World world){
        StructureGenerator gen = cfg().RESOURCE_WORLD_STRUCTURE_1.value();
        if(gen == null) return;

        //dialogListener().dialogPlayer().pushDialog(cfg().ENTER_RESOURCE_WORLD_DIALOG.value());

        GuideStructurePlacer placer = new GuideStructurePlacer(p.getChunk(), 48, gen, cfg().RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE.value(),
            () -> p.isOnline() && p.getWorld().equals(world));
        placer.placeStructure().orTimeout(90L, TimeUnit.SECONDS).whenComplete((loc, throwable) ->{
            if(throwable != null){
                throwable.printStackTrace();
                if(throwable instanceof TimeoutException){
                    p.sendMessage(Component.text("The Guide structure was not able to be placed in time. This is usually due to new chunks taking a long time to load in. Please go back to Spawn and re-travel to the Resource Overworld to try again! It's recommended that you fast travel to the same spot."));
                    return;
                }
                p.sendMessage(Component.text("An error occurred. Contact developer. CODE 7392"));
                return;
            }

            if(!p.isOnline()){
                if(loc != null) Crux.logError(p.getName() + " guide structure was placed even though they are offline!");
                return;
            }
            if(loc == null){
                Lang.GUIDE_STRUCTURE_CANNOT_BE_PLACED.use(p);
                return;
            }
            completeStep(() ->{
                setFallenMeteorLocation(loc);
            });
            //dialogListener().dialogPlayer().pushDialog(cfg().STRUCTURE_LOCATED_DIALOG.value());
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        if(!isPlayer(event)) return;
        World from = event.getFrom();
        World world = p.getWorld();
        if(npc != null){
            npc.remove();
            npc = null;
            npcGoal = null;
            if(isSpawn(from)) showEntities(from);
        }

        if(isResourceOverworld(world)){
            if(!hasEnteredResourceOverworld() || fallenMeteorLocation == null){
                completeStep(() ->{
                    state.grantFlag(Flags.ENTERED_RESOURCE_OVERWORLD.flag());
                });
                return;
            }
        }

        if(canSetup()){
            Crux.scheduler().runTaskLater(() ->{
                if(!canSetup()) return;
                if(!p.isOnline()) return;
                setup();
            }, 5L);
            return;
        }
        testDialog();
    }

    public boolean isResourceOverworld(World world){
        CruxWorld cruxWorld = CruxCore.core().worldManager().getWorld(world.key());
        if(cruxWorld==null) return false;
        CruxWorldType type = cruxWorld.get(CruxWorldsComponents.WORLD_TYPE);
        if(!USurviveWorldTypes.RESOURCE_OVERWORLD.compare(type)) return false;
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        if(!isStoryMob(event.getEntity())) return;
        completeStep(() ->{
            state.incrementFlag(NumFlags.COUNT_ENEMY_KILL.flag(), 1);
        });
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPortalEnter(EntityPortalEnterEvent event) {
        if(!event.getEntity().equals(npc)) return;
        if(data.state.hasFlag(DataFlags.PUSHED_NPC_IN_PORTAL.flag())) return;
        data.state.grantFlag(DataFlags.PUSHED_NPC_IN_PORTAL.flag());
        if(npcGoal != null){
            npcGoal.getDialogPlayer().pushDialog(cfg().PUSHED_NPC_IN_PORTAL_DIALOG.value());
            return;
        }
        dialogListener().dialogPlayer().pushDialog(cfg().PUSHED_NPC_IN_PORTAL_DIALOG.value());
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!event.getDamager().equals(p)) return;
        if(event.getEntity().equals(npc)){
            if(data.state.hasFlag(DataFlags.PUNCHED_NPC.flag())) return;
            data.state.grantFlag(DataFlags.PUNCHED_NPC.flag());
            if(npcGoal != null){
                npcGoal.getDialogPlayer().pushDialog(cfg().PUNCHED_NPC_DIALOG.value());
                return;
            }
            dialogListener().dialogPlayer().pushDialog(cfg().PUNCHED_NPC_DIALOG.value());
        }
    }


    public boolean hasEnteredResourceOverworld() {
        return state.hasFlag(Flags.ENTERED_RESOURCE_OVERWORLD.flag());
    }
}
