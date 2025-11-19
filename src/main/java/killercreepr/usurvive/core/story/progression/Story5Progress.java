package killercreepr.usurvive.core.story.progression;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.core.Crux;
import killercreepr.cruxchat.api.event.EntityBadgeGrantEvent;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import killercreepr.usurvive.api.story.*;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.config.story.Story5Config;
import killercreepr.usurvive.core.dialog.WrappedDialogContent;
import killercreepr.usurvive.core.entity.mob.goals.guide.GuidePathGoal;
import killercreepr.usurvive.core.story.SimpleStoryState;
import killercreepr.usurvive.core.story.progression.data.GenericStoryProgressData;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Story5Progress extends StoryHolderStoryProgress implements Listener {
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
        RANKED_UP,
        RANKED_UP_DIALOG,
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
                return state.hasFlag(Flags.RANKED_UP.flag());
            }

            @Override
            public DialogContent getDialog() {
                return cfg().START_DIALOG.value();
            }
        },

        new StoryStep() {
            @Override
            public boolean canStart(StoryState state) {
                return state.hasFlag(Flags.RANKED_UP.flag());
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.RANKED_UP_DIALOG.flag());
            }

            @Override
            public DialogContent getDialog() {
                return new WrappedDialogContent(cfg().RANK_UP_DIALOG.value()){
                    @Override
                    public void onDialogEnd(DialogPlayer player) {
                        super.onDialogEnd(player);
                        completeStep(() ->{
                           state.grantFlag(Flags.RANKED_UP_DIALOG.flag());
                        });
                    }
                };
            }
        },

        new StoryStep() {
            @Override
            public boolean canStart(StoryState state) {
                return state.hasFlag(Flags.RANKED_UP_DIALOG.flag());
            }

            @Override
            public boolean isComplete(StoryState state) {
                return state.hasFlag(Flags.TALKED_WITH_NPC.flag());
            }
        }
    );

    //ustory interact <player> npc=guide_1
    @Override
    public void onInfoInteraction(DataExchange info) {
        super.onInfoInteraction(info);
        String npc = info.get("npc", String.class);
        if(npc == null) return;
        if(!"guide_1".equalsIgnoreCase(npc)) return;
        if(state.hasFlag(Flags.RANKED_UP_DIALOG.flag())){
            completeStep(() ->{
                state.grantFlag(Flags.TALKED_WITH_NPC.flag());
            });
        }
    }

    @Override
    public List<StoryStep> getSteps() {
        return steps;
    }

    @Override
    public StoryState state() {
        return state;
    }

    public static final Key KEY = Crux.key("5");

    public static class Loader implements FileStoryProgressionLoader<Story5Progress, Data>{
        public static final FileObjectHandler<Story5Progress> tempHandler = new AutoFileHandler<>(Story5Progress.class);
        public static final FileObjectHandler<Data> permHandler = new AutoFileHandler<>(Data.class);

        @Override
        public FileObjectHandler<Story5Progress> tempDataHandler() {
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
    public Story5Config cfg() {
        return USurvivePlugin.inst().cfgContainer().STORY_5;
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
        //todo
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
    public void onEntityBadgeGrant(EntityBadgeGrantEvent event) {
        if(!isPlayer(event.getEntity())) return;
        if(!event.getChatBadge().key().value().startsWith("rank/")) return;
        try{
            Integer.parseInt(event.getChatBadge().key().value().replace("rank/", ""));
            completeStep(() ->{
                state.grantFlag(Flags.RANKED_UP.flag());
            });
        } catch (IllegalArgumentException e) {
        }
    }

}
