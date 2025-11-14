package killercreepr.usurvive.core.story.progression;

import killercreepr.cruxblocks.api.mining.user.Miner;
import killercreepr.cruxblocks.core.mining.user.EntityMiner;
import killercreepr.usurvive.api.dialog.listener.DialogListener;
import killercreepr.usurvive.api.story.StoryHolder;
import killercreepr.usurvive.api.story.StoryProgression;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import java.util.UUID;

public abstract class StoryHolderStoryProgress implements StoryProgression {
    protected transient StoryHolder holder;
    protected transient Player p;

    public DialogListener dialogListener(){
        return DialogListener.listener(p);
    }

    protected boolean isPlayer(Miner miner){
        if(!(miner instanceof EntityMiner m)) return false;
        return isPlayer(m.getEntity());
    }

    protected boolean isPlayer(PlayerEvent event){
        return isPlayer(event.getPlayer());
    }
    protected boolean isPlayer(Entity e){
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
}
