package killercreepr.usurvive.core.story.progression;

import killercreepr.usurvive.api.story.StoryHolder;
import killercreepr.usurvive.api.story.StoryProgression;
import org.bukkit.entity.Player;

public abstract class StoryHolderStoryProgress implements StoryProgression {
    protected transient StoryHolder holder;
    protected transient Player p;

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
