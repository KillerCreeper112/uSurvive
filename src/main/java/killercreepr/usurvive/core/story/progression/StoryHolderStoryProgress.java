package killercreepr.usurvive.core.story.progression;

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
