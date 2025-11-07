package killercreepr.usurvive.core.entity.mob.goals.guide;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalPath;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GuidePathGoal extends CruxMobGoal implements PathTargetMobGoal {
    protected final PathTargetMobGoal pathTarget = PathTargetMobGoal.pathTargetMobGoal(this, 0.5);
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
        this.user = user;
    }

    protected Player user;
    protected List<String> dialog;
    protected String currentDialogLine;
    protected String[] currentDialogSplit;
    protected int dialogWait;

    public List<String> getDialog() {
        return dialog;
    }

    public void setDialog(List<String> dialog) {
        this.dialog = dialog;
        this.dialogIndex = 0;
    }

    protected int dialogIndex = 0;
    protected int dialogSplitIndex = 0;

    @Override
    public boolean isValidTarget(@NotNull LivingEntity target) {
        return false;
    }

    public void dialogTick(){
        if(dialog == null) return;
        mob.lookAt(user);
        if(dialogWait > 0){
            dialogWait--;
            if(currentDialogLine != null){
                user.sendActionBar(currentDialogLine);
            }
            return;
        }

        String dialog = getToSendDialogLine();
        user.sendActionBar(dialog);
        user.playSound(user, Sound.BLOCK_WOOD_BREAK, .5f, 1f);
    }

    public String getToSendDialogLine(){
        if(currentDialogLine == null){
            if(dialogIndex >= dialog.size()){
                setDialog(null);
                return null;
            }
            currentDialogSplit = dialog.get(dialogIndex).split(" ");
            currentDialogLine = currentDialogSplit[0];
            return currentDialogLine;
        }
        dialogSplitIndex++;
        if(dialogSplitIndex >= currentDialogSplit.length){
            dialogSplitIndex = 0;
            dialogIndex++;
            currentDialogLine = null;
            dialogWait = 10;
            return null;
        }
        dialogWait = 1;
        currentDialogLine += " " + currentDialogSplit[dialogSplitIndex];
        return currentDialogLine;
    }

    @Override
    public void tick() {
        if(dialog != null){
            dialogTick();
            return;
        }
        this.pathTarget.tick();
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
