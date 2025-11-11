package killercreepr.usurvive.core.entity.mob.goals.guide;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxString;
import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalNode;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalPath;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import killercreepr.usurvive.api.entity.mob.goal.path.GuidePathGoalNode;
import killercreepr.usurvive.api.guide.GuidePoint;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.dialog.DialogContentLinePair;
import killercreepr.usurvive.core.dialog.UserDialogPlayer;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettingSelections;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettings;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GuidePathGoal extends CruxMobGoal implements PathTargetMobGoal {
    protected final PathTargetMobGoal pathTarget = PathTargetMobGoal.pathTargetMobGoal(this, 1);
    public GuidePathGoal(@NotNull Mob mob) {
        super(mob);
    }

    public GuidePathGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
    }

    public Player getUser() {
        return user;
    }

    public void setUser(Player user) {
        if(this.user != null){
            dialogPlayer.removeAudience(this.user);
        }
        this.user = user;
        if(user != null){
            dialogPlayer.addAudience(user);
        }
    }

    protected TextDisplay dialogDisplay;

    public TextDisplay dialogDisplay(){
        if(dialogDisplay != null) return dialogDisplay;
        Location loc = getDialogLocation();
        TextDisplay text = loc.getWorld().spawn(loc, TextDisplay.class, e ->{
            e.setSeeThrough(true);
            e.setBillboard(Display.Billboard.CENTER);
            e.setVisibleByDefault(false);
        });
        if(user != null) user.showEntity(Crux.getMainPlugin(), text);
        return dialogDisplay = text;
    }

    public Location getDialogLocation(){
        return mob.getLocation().add(0, 0.5 + mob.getHeight(), 0);
    }

    public void updateText(TextDisplay display, String msg){
        if(msg == null || msg.isEmpty()){
            display.text(null);
            return;
        }
        List<String> format = USurvivePlugin.inst().values().TEXT_DISPLAY_DIALOG_FORMAT().value();
        if(format == null) return;
        var tags = TagContainer.merged()
            .hook(new DialogContentLinePair(dialogPlayer.getDialog(), dialogPlayer.getCurrentLine()))
            .add(Tag.parsed("line", msg));

        tags.add(Tag.stringList("line", (args, ctx) ->{
            String lineFormat = USurvivePlugin.inst().values().TEXT_DISPLAY_DIALOG_FORMAT_LINE().value();
            if(lineFormat == null) return null;
            String msgToFormat = ctx.deserializeString(lineFormat, tags);
            List<String> list = new ArrayList<>();
            CruxString.buildDescription(
                msgToFormat, list::add,
                30
            );
            return list;
        }));

        List<String> formatted = Crux.format().deserializeStringList(format, tags);
        String finalMsg = String.join("\n", formatted);
        display.text(Crux.format().deserialize(finalMsg));

        /*StringBuilder builder = new StringBuilder();
        CruxString.buildDescription(
            msg, line ->{
                builder.append(line);
            },
            line ->{
                builder.append("\n");
            },
            30
        );
        display.text(Crux.format().deserialize(
            "" + builder.toString(), TagContainer.string().hook(new DialogContentLinePair(dialogPlayer.getDialog(), dialogPlayer.getCurrentLine()))
        ));*/
    }

    public boolean isSpeaking(){
        return dialogPlayer.getDialog() != null;
    }

    @Override
    public boolean isValidTarget(@NotNull LivingEntity target) {
        return false;
    }

    protected GuidePoint nextGuidePoint;

    public GuidePoint getNextGuidePoint() {
        return nextGuidePoint;
    }

    public void setNextGuidePoint(GuidePoint nextGuidePoint) {
        this.nextGuidePoint = nextGuidePoint;
    }

    public DialogPlayer getDialogPlayer() {
        return dialogPlayer;
    }

    protected Player user;
    protected GuidePoint guidePoint;
    protected DialogPlayer dialogPlayer = new UserDialogPlayer(){
        @Override
        public void onDialogTicking() {
            super.onDialogTicking();
            if(user == null) return;
            if(!user.getWorld().equals(mob.getWorld())) return;
            mob.lookAt(user);
        }

        @Override
        public void onDialogSpeaking() {
            super.onDialogSpeaking();
            updateText(dialogDisplay(), currentDialogLineFor(user));
        }

        @Override
        public int dialogSplitSpeedValue(int value) {
            return Math.round(super.dialogSplitSpeedValue(value) / 2f);
        }

        @Override
        public int dialogWaitTimeValue(int value) {
            return Math.round(super.dialogWaitTimeValue(value) / 2f);
        }
    };

    public void dialogTick(){
        if(user == null) return;
        dialogPlayer.dialogTick();
    }

    @Override
    protected void onRemovalOrDeath(boolean died) {
        super.onRemovalOrDeath(died);
        if(dialogDisplay != null) dialogDisplay.remove();
    }

    @Override
    public void tick() {
        dialogTick();
        if(dialogPlayer.getDialog() != null){
            if(dialogDisplay != null){
                dialogDisplay.teleport(getDialogLocation());
            }
            if(guidePoint == null || !guidePoint.keepMoving()) return;

            if(nextGuidePoint != null){
                if(this.pathTarget.hasPath() && this.pathTarget.getPath().getNextNode() instanceof GuidePathGoalNode n && n.guidePoint().dialog() != null){
                    return;
                }
            }
        }else{
            if(dialogDisplay != null){
                dialogDisplay.remove();
                dialogDisplay = null;
            }

            if(nextGuidePoint != null){
                guidePoint = nextGuidePoint;
                nextGuidePoint = null;


                if(guidePoint.dialog() != null) dialogPlayer.queueDialog(guidePoint.dialog());
                return;
            }
        }

        if(!userDistanceCheck()){
            mob.getPathfinder().stopPathfinding();
            if(user.getWorld().equals(mob.getWorld())) mob.lookAt(user);
            return;
        }

        this.pathTarget.tick();
    }

    public boolean userDistanceCheck(){
        if(user == null) return true;
        if(!user.getWorld().equals(mob.getWorld())) return false;
        double distanceFromUser = user.getLocation().distanceSquared(mob.getLocation());
        if(distanceFromUser <= (8*8)) return true;

        int goalIndex = pathTarget.getPath() == null ? 0 : pathTarget.getPath().getCurrentNodeIndex();
        NavigableMap<Double, Integer> closestPath = getUserCloseToPath();
        int index = 0;
        for (Map.Entry<Double, Integer> entry : closestPath.entrySet()) {
            index++;
            if(entry.getValue() >= goalIndex) return true;

            if(index > 1) break;
        }
        return false;
    }

    public NavigableMap<Double, Integer> getUserCloseToPath(){
        if(pathTarget.getPath() == null) return new TreeMap<>();
        NavigableMap<Double, Integer> map = new TreeMap<>();
        int index = -1;
        for (GoalNode node : pathTarget.getPath().getNodes()) {
            index++;
            Location loc = new Location(mob.getWorld(), node.x(), node.y(), node.z());
            double distance = loc.distanceSquared(user.getLocation());
            map.put(distance, index);
        }
        return map;
    }

    public boolean isUserClose(double distance){
        distance = distance * distance;
        return user.getLocation().distanceSquared(mob.getLocation()) <= distance;
    }

    @Override
    public @Nullable GoalPath getPath() {
        return pathTarget.getPath();
    }

    @Override
    public void setPath(@Nullable GoalPath goalPath) {
        pathTarget.setPath(goalPath);
    }
}
