package killercreepr.usurvive.core.story.progression;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.data.WorldPosition;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.math.BlockPos;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxadvancements.api.advancement.CruxAdvancement;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.api.event.CruxAdvancementGrantEvent;
import killercreepr.cruxadvancements.core.entity.memory.AdvancementHolder;
import killercreepr.cruxadvancements.core.registries.AdvancementRegistries;
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
import killercreepr.usurvive.api.entity.mob.goal.path.GuidePathGoalNode;
import killercreepr.usurvive.api.guide.GuidePoint;
import killercreepr.usurvive.api.story.FileStoryProgressionLoader;
import killercreepr.usurvive.api.story.StoryHolder;
import killercreepr.usurvive.api.story.StoryProgressData;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.advancement.objective.GuideDiscoverMeteorObjective;
import killercreepr.usurvive.core.config.story.Story1Config;
import killercreepr.usurvive.core.data.DeathGuide;
import killercreepr.usurvive.core.dialog.WrappedDialogContent;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.entity.mob.goals.guide.GuidePathGoal;
import killercreepr.usurvive.core.guide.GuideAdvances;
import killercreepr.usurvive.core.guide.GuideStructurePlacer;
import killercreepr.usurvive.core.guide.GuideUtil;
import killercreepr.usurvive.core.guide.particle.TrailRenderer;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.pathfinding.GuidePathfinder;
import killercreepr.usurvive.core.story.StoryAdvances;
import killercreepr.usurvive.core.story.progression.data.GenericStoryProgressData;
import killercreepr.usurvive.core.util.AdvanceUtil;
import killercreepr.usurvive.core.world.USurviveWorldTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        public boolean punchedNPC;

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
    public boolean enemiesSpawned;

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

    @Override
    public void onStart(Player p, StoryHolder holder) {
        super.onStart(p, holder);
        if(hasEnteredResourceOverworld() || !p.getWorld().key().equals(cfg().NPC_POINTS_1.value().getFirst().pos().worldKey())) return;
        WorldPosition spawn = cfg().NPC_SPAWN_1.value();
        p.teleport(spawn.toLocation());
    }

    @Override
    public void onTick() {
        super.onTick();
        int every = 30;//cfg.DEATH_GUIDE_TICK_EVERY().value().intValue();
        if(every==0) return;
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
            if(!AdvanceUtil.hasAnyAdvancement(p, StoryAdvances.DISCOVER_GUIDE_METEOR, GuideAdvances.GUIDE_SHARD_CLAIM)){
                setFallenMeteorLocation(null);

                Lang.GUIDE_WORLD_RESET.use(p);
                CruxAdvancementManager manager = AdvancementRegistries.ADVANCEMENT_MANAGERS.get(USurvivePlugin.inst().key("story"));
                if(manager == null) return;

                var advancement = manager.getAdvancement(StoryAdvances.ENTER_RESOURCE_OVERWORLD);
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
        var advancement = manager.getAdvancement(StoryAdvances.DISCOVER_GUIDE_METEOR);
        if(advancement == null) return;

        if(advancement.isGranted(p)){
            if(!enemiesSpawned){
                manager.revokeAdvancement(p, advancement);
            }else return;
        }

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
                    var content = cfg().STRUCTURE_NEARBY_DIALOG.value();
                    if(content != null){
                        content = new WrappedDialogContent(content){
                            @Override
                            public void onSendInfo(DataExchange info) {
                                String action = info.get("initiate", String.class);
                                if(action == null) return;
                                switch (action.toLowerCase()){
                                    case "spawn_bounders" ->{
                                        if(!(CruxRegistries.TYPED_DATA_COMPONENTS.get(Crux.key(info.get("spawner_component", String.class))).getValue()
                                            instanceof EntitySpawnerComponent spawner)){
                                            return;
                                        }
                                        Block b = fallenMeteorLocation.getBlock(world);
                                        new BlockEntitySpawner(
                                            b,
                                            spawner, spawner.spawner
                                        ).tick();
                                        enemiesSpawned = true;

                                        dialogListener().dialogPlayer().pushDialog(cfg().BOUNDERS_INVADE_SPAWNED_DIALOG.value());
                                    }
                                }

                            }
                        };
                        dialogListener().dialogPlayer().pushDialog(content);
                    }
                    //Lang.GUIDE_STRUCTURE_NEARBY.use(p);
                }
                stopTickDistance = false;
            });
        }
    }

    private AdvancementHolder holder(@NotNull Entity p){
        return AdvancementHolder.holderIfLoaded(p);
    }

    public void tickStructureGuide(@NotNull Player p){
        if(fallenMeteorLocation == null) return;
        if(!p.getWorld().key().equals(fallenMeteorWorld)) return;
        if(GuideUtil.hasGuideDisabled(p)) return;
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCruxAdvancementGrant(CruxAdvancementGrantEvent event) {
        CruxAdvancement advancement = event.getAdvancement();

        UUID who = event.getWho();
        if(!isPlayer(who)) return;
        if(advancement.key().equals(StoryAdvances.ENTER_RESOURCE_OVERWORLD)){
            onEnterResourceOverworld(event);
            return;
        }
    }

    public void onEnterResourceOverworld(CruxAdvancementGrantEvent event){
        CruxAdvancementManager manager = event.getManager();
        UUID who = event.getWho();

        World world = p.getWorld();
        CruxWorld cruxWorld = CruxCore.core().worldManager().getWorld(world.key());
        if(cruxWorld==null) return;
        CruxWorldType type = cruxWorld.get(CruxWorldsComponents.WORLD_TYPE);
        if(!USurviveWorldTypes.RESOURCE_OVERWORLD.compare(type)) return;

        StructureGenerator gen = cfg().RESOURCE_WORLD_STRUCTURE_1.value();
        if(gen == null) return;

        setEnteredResourceOverworld(true);

        GuideStructurePlacer placer = new GuideStructurePlacer(p.getChunk(), 48, gen, cfg().RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE.value(),
            () -> p.isOnline() && p.getWorld().equals(world));
        placer.placeStructure().orTimeout(90L, TimeUnit.SECONDS).whenComplete((loc, throwable) ->{
            if(throwable != null){
                throwable.printStackTrace();
                if(throwable instanceof TimeoutException){
                    manager.revokeAdvancement(who, event.getAdvancement());
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
                manager.revokeAdvancement(who, event.getAdvancement());
                Lang.GUIDE_STRUCTURE_CANNOT_BE_PLACED.use(p);
                return;
            }
            setFallenMeteorLocation(loc);
            dialogListener().dialogPlayer().pushDialog(cfg().STRUCTURE_LOCATED_DIALOG.value());
            //Lang.GUIDE_STRUCTURE_PLACED.use(p);
        });
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPortalEnter(EntityPortalEnterEvent event) {
        if(!event.getEntity().equals(npc)) return;
        if(data.pushedNPCInPortal) return;
        data.pushedNPCInPortal = true;
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
            if(data.punchedNPC) return;
            if(npcGoal != null){
                npcGoal.getDialogPlayer().pushDialog(cfg().PUNCHED_NPC_DIALOG.value());
                return;
            }
            data.punchedNPC = true;
            dialogListener().dialogPlayer().pushDialog(cfg().PUNCHED_NPC_DIALOG.value());
        }
    }


    public boolean hasEnteredResourceOverworld() {
        return enteredResourceOverworld;
    }

    public void onEnteredResourceOverworld(){
        if(p == null) return;
        dialogListener().dialogPlayer().pushDialog(cfg().ENTER_RESOURCE_WORLD_DIALOG.value());
    }

    public void setEnteredResourceOverworld(boolean enteredResourceOverworld) {
        if(!this.enteredResourceOverworld && enteredResourceOverworld){
            onEnteredResourceOverworld();
        }
        this.enteredResourceOverworld = enteredResourceOverworld;
    }
}
