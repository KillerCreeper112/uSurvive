package killercreepr.usurvive.core.story.progression;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxcurrency.api.event.CurrencyDepositEvent;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.story.*;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.block.ActiveOrbitConduit;
import killercreepr.usurvive.core.config.story.Story2Config;
import killercreepr.usurvive.core.entity.mob.goals.guide.GuidePathGoal;
import killercreepr.usurvive.core.guide.particle.TrailRenderer;
import killercreepr.usurvive.core.pathfinding.GuidePathfinder;
import killercreepr.usurvive.core.story.SimpleStoryState;
import killercreepr.usurvive.core.story.progression.data.GenericStoryProgressData;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Story2Progress extends StoryHolderStoryProgress implements Listener {
    public enum NumFlags{
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
        WENT_TO_SPAWN,
        TALKED_TO_NPC_1,
        DEPOSITED_ORBIT,
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
                return true;
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.WENT_TO_SPAWN.flag());
            }

            @Override
            public DialogContent getDialog() {
                return cfg().START_DIALOG.value();
            }
        },

        new StoryStep() {
            @Override
            public boolean canStart(StoryState state) {
                return state.hasFlag(Flags.WENT_TO_SPAWN.flag());
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.TALKED_TO_NPC_1.flag());
            }

            @Override
            public DialogContent getDialog() {
                return cfg().WENT_TO_DIALOG.value();
            }
        },

        new StoryStep() {
            @Override
            public boolean canStart(StoryState state) {
                return state.hasFlag(Flags.TALKED_TO_NPC_1.flag());
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.DEPOSITED_ORBIT.flag());
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

    public static final Key KEY = Crux.key("2");

    public static class Loader implements FileStoryProgressionLoader<Story2Progress, Data>{
        public static final FileObjectHandler<Story2Progress> tempHandler = new AutoFileHandler<>(Story2Progress.class);
        public static final FileObjectHandler<Data> permHandler = new AutoFileHandler<>(Data.class);

        @Override
        public FileObjectHandler<Story2Progress> tempDataHandler() {
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

    protected transient Mob npc;
    protected transient GuidePathGoal npcGoal;

    protected transient long lastCheckedConduit;
    protected transient Block conduit;
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
    public Story2Config cfg() {
        return USurvivePlugin.inst().cfgContainer().STORY_2;
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
       /* npc = (Mob) USurviveMobs.GUIDE_1_NPC.spawn(cfg().NPC_POINTS_1.value().getFirst().pos().toLocation());
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
        npcGoal.setPath(GoalPath.goalPath(nodes));*/
    }

    public void setupNPC(){
        if(npc != null) return;
        setupNPC(p);
    }

    @Override
    public void onLoaded(Player p, StoryHolder holder) {
        super.onLoaded(p, holder);
        restorePlayer(p);
    }

    @Override
    public void onTick() {
        super.onTick();
        int every = 30;
        if(Crux.getCurrentTick() % every != 0) return;
        if(!state.hasFlag(Flags.TALKED_TO_NPC_1.flag()) || state.hasFlag(Flags.DEPOSITED_ORBIT.flag())) return;
        tickStructureGuide(p);
    }

    public void tickStructureGuide(@NotNull Player p){
        if(conduit == null){
            if(!isSpawn(p.getWorld())) return;
            if(CruxMath.hasOccurredWithin(lastCheckedConduit, 80L)) return;
            findAndSetConduit(p.getLocation().getBlock());
            return;
        }
        if(!conduit.getWorld().equals(p.getWorld())) return;
        Location pos =  conduit.getLocation();

        List<Location> path = new GuidePathfinder(p).findPath(p.getLocation(), pos, 0.5D,
            USurvivePlugin.inst().cfgContainer().STORY_1.MAX_PARTICLE_POINTS.value().value().intValue());
        TrailRenderer.renderTrail(p, path);
    }

    public boolean isSpawn(World world){
        return world.getName().equals(USurvivePlugin.inst().values().SPAWN_WORLD().value());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if(!isPlayer(event)) return;
        World world = p.getWorld();
        if(isSpawn(world)){
            if(!state.hasFlag(Flags.WENT_TO_SPAWN.flag())){
                completeStep(() ->{
                    state.grantFlag(Flags.WENT_TO_SPAWN.flag());
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCurrencyDeposit(CurrencyDepositEvent event) {
        if(!isPlayer(event.getPlayer())) return;
        if(!event.getCurrency().key().equals(Crux.key("orbit"))) return;
        if(!event.getReason().equalsIgnoreCase("usurvive:menu/currency_deposit")) return;
        Player p = event.getPlayer().getPlayer();
        if(p == null) return;

        completeStep(() ->{
            state.grantFlag(Flags.DEPOSITED_ORBIT.flag());
        });
    }

}
