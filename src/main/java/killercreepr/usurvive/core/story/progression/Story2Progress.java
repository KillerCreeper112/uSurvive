package killercreepr.usurvive.core.story.progression;

import killercreepr.crux.core.Crux;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.story.*;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.config.story.Story2Config;
import killercreepr.usurvive.core.entity.mob.goals.guide.GuidePathGoal;
import killercreepr.usurvive.core.story.SimpleStoryState;
import killercreepr.usurvive.core.story.progression.data.GenericStoryProgressData;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Story2Progress extends StoryHolderStoryProgress implements Listener {

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
    protected List<StoryFlag> cacheRequiredFlags;
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
    );

    public StoryStep nextStep(Player p){
        StoryStep step = getNextStep(state);

        if (step != null) {
            if(step.getDialog() != null){
                dialogListener().dialogPlayer().setDialog(step.getDialog());
            }
            step.onStart(p);
        }
        return step;
    }

    public StoryStep completeStep(Runnable consumer){
        StoryStep current = getNextStep(state);
        consumer.run();
        if(current != null && current.isComplete(state)){
            current.onComplete(p);
        }
        return nextStep(p);
    }

    public StoryStep getNextStep(StoryState state) {
        for (StoryStep step : getSteps()) {
            if (!step.isComplete(state) && step.canStart(state))
                return step;
        }
        return null;
    }

    public void restorePlayer(Player p) {
        nextStep(p);
    }


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
    }

    public void nextStage(){
        holder.complete(this);
        //todo holder.addProg
    }
}
