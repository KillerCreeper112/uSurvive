package killercreepr.usurvive.core.story.progression;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.cruxshops.api.event.EntityPurchaseTraderTradeEvent;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import killercreepr.usurvive.api.story.*;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.config.story.Story3Config;
import killercreepr.usurvive.core.dialog.WrappedDialogContent;
import killercreepr.usurvive.core.entity.mob.goals.guide.GuidePathGoal;
import killercreepr.usurvive.core.story.SimpleStoryState;
import killercreepr.usurvive.core.story.progression.data.GenericStoryProgressData;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Story3Progress extends ExclaimHolderStoryProgress {
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
        MERCHANT_BOUGHT,
        COMMANDS_EXECUTED,
        COMPLETED_BOUGHT_DIALOG,
        TALKED_WITH_NPC,
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
                return state.hasFlag(Flags.COMMANDS_EXECUTED.flag());
            }

            @Override
            public DialogContent getDialog() {
                return new WrappedDialogContent(cfg().START_DIALOG.value()){
                    @Override
                    public void onDialogEnd(DialogPlayer player) {
                        super.onDialogEnd(player);
                        if(state.hasFlag(Flags.COMMANDS_EXECUTED.flag())) return;
                        Crux.scheduler().runTaskMain(() ->{
                            completeStep(() ->{
                                state.grantFlag(Flags.COMMANDS_EXECUTED.flag());
                                Crux.format().deserializeStringList(cfg().GIVE_COMMANDS.valueOr(List.of()), TagContainer.merged().hook(p))
                                    .forEach(s ->{
                                        Crux.getServer().dispatchCommand(Crux.getServer().getConsoleSender(), s);
                                    });
                            });
                        });
                    }
                };
            }
        },
        new StoryStep() {
            @Override
            public boolean canStart(StoryState state) {
                return state.hasFlag(Flags.COMMANDS_EXECUTED.flag()) && state.hasFlag(Flags.MERCHANT_BOUGHT.flag());
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.COMPLETED_BOUGHT_DIALOG.flag());
            }

            @Override
            public DialogContent getDialog() {
                return new WrappedDialogContent(cfg().MERCHANT_BOUGHT_DIALOG.value()){
                    @Override
                    public void onDialogEnd(DialogPlayer player) {
                        super.onDialogEnd(player);
                        completeStep(() ->{
                            state.grantFlag(Flags.COMPLETED_BOUGHT_DIALOG.flag());
                        });
                    }
                };
            }
        },
        new StoryStep.ChangedWorld() {
            @Override
            public void onChangedWorld(World from) {
                if(isSpawn(p.getWorld())) Crux.scheduler().runTask(() -> spawnGuide1Exclaim(p.getWorld()));
            }

            @Override
            public boolean canStart(StoryState state) {
                return state.hasFlag(Flags.COMPLETED_BOUGHT_DIALOG.flag());
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.TALKED_WITH_NPC.flag());
            }

            @Override
            public void onStart(Player p) {
                ChangedWorld.super.onStart(p);
                if(isSpawn(p.getWorld())) spawnGuide1Exclaim(p.getWorld());
            }
        }
    );

    @Override
    public void onStepCompleted(StoryStep step) {
        super.onStepCompleted(step);
        clearTexts();
    }

    //ustory interact <player> npc=guide_1
    @Override
    public void onInfoInteraction(DataExchange info) {
        super.onInfoInteraction(info);
        String npc = info.get("npc", String.class);
        if(npc == null) return;
        if(!"guide_1".equalsIgnoreCase(npc)) return;
        completeStep(() ->{
            state.grantFlag(Flags.TALKED_WITH_NPC.flag());
        });
    }

    @Override
    public List<StoryStep> getSteps() {
        return steps;
    }

    @Override
    public StoryState state() {
        return state;
    }

    public static final Key KEY = Crux.key("3");

    public static class Loader implements FileStoryProgressionLoader<Story3Progress, Data>{
        public static final FileObjectHandler<Story3Progress> tempHandler = new AutoFileHandler<>(Story3Progress.class);
        public static final FileObjectHandler<Data> permHandler = new AutoFileHandler<>(Data.class);

        @Override
        public FileObjectHandler<Story3Progress> tempDataHandler() {
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
    public Story3Config cfg() {
        return USurvivePlugin.inst().cfgContainer().STORY_3;
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
        holder.addProgression(new Story4Progress());
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPurchaseTraderTrade(EntityPurchaseTraderTradeEvent event) {
        if(!isPlayer(event.getEntity())) return;
        completeStep(() ->{
            state.grantFlag(Flags.MERCHANT_BOUGHT.flag());
        });
    }

}
