package killercreepr.usurvive.core.story.progression;

import killercreepr.cruxblocks.api.mining.user.Miner;
import killercreepr.cruxblocks.core.mining.user.EntityMiner;
import killercreepr.usurvive.api.dialog.listener.DialogListener;
import killercreepr.usurvive.api.story.StoryHolder;
import killercreepr.usurvive.api.story.StoryProgression;
import killercreepr.usurvive.api.story.StoryState;
import killercreepr.usurvive.api.story.StoryStep;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public abstract class StoryHolderStoryProgress implements StoryProgression {
    protected transient StoryHolder holder;
    protected transient Player p;
    protected transient Collection<StoryStep> stepsAlreadyPlayedDialog = new HashSet<>();

    public StoryStep nextStep(Player p){
        StoryStep step = getNextStep(state());

        if (step != null) {
            if(step.getDialog() != null){
                dialogListener().dialogPlayer().setDialog(step.getDialog());
            }
            step.onStart(p);
            onStepStarted(step);
        }
        return step;
    }

    public void nextStage(){
        holder.complete(this);
    }

    @Override
    public void onStepCompleted(StoryStep step) {
        if(hasAllFlags()){
            nextStage();
        }
    }

    public StoryStep completeStep(Runnable consumer){
        StoryStep current = getNextStep(state());
        consumer.run();
        if(current != null && current.isComplete(state())){
            current.onComplete(p);
            onStepCompleted(current);
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

    public DialogListener dialogListener(){
        return DialogListener.listener(p);
    }

    protected boolean isPlayer(Miner miner){
        if(!(miner instanceof EntityMiner m)) return false;
        return isPlayer(m.getEntity());
    }

    protected boolean isPlayer(PlayerEvent event){
        return isPlayer((Entity) event.getPlayer());
    }
    protected boolean isPlayer(Entity e){
        return e != null && isPlayer(e.getUniqueId());
    }
    protected boolean isPlayer(OfflinePlayer e){
        return e != null && isPlayer(e.getUniqueId());
    }

    protected boolean isPlayer(UUID uuid){
        return uuid.equals(p == null ? null : p.getUniqueId());
    }

    @Override
    public void onStart(Player p, StoryHolder holder) {
        if(this.p == null){
            this.p = p;
            this.holder = holder;
        }
        StoryProgression.super.onStart(p, holder);
    }

    @Override
    public void onLoaded(Player p, StoryHolder holder) {
        this.p = p;
        this.holder = holder;
        StoryProgression.super.onLoaded(p, holder);
    }

    public Player getPlayer() {
        return p;
    }

    public StoryHolder getHolder() {
        return holder;
    }

    public boolean hasAlreadyPlayedDialog(StoryStep step){
        return stepsAlreadyPlayedDialog.contains(step);
    }

    public boolean testDialog(){
        var current = getNextStep(state());
        if(current == null) return false;
        var dialog = current.getDialog();
        if(dialog == null || !dialog.hasCustomCanPlayLogic()) return false;
        if(hasAlreadyPlayedDialog(current)) return false;
        dialogListener().dialogPlayer().pushDialog(dialog);
        return true;
    }
}
