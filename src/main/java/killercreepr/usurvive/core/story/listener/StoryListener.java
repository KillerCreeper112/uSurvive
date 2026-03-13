package killercreepr.usurvive.core.story.listener;

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import killercreepr.usurvive.api.story.StoryHolder;
import killercreepr.usurvive.api.values.ValuesProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Deprecated
public class StoryListener implements Listener {
    protected final ValuesProvider cfg;

    public StoryListener(ValuesProvider cfg) {
        this.cfg = cfg;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerClientLoadedWorld(PlayerClientLoadedWorldEvent event) {
        Player p = event.getPlayer();
        var holder = getStoryHolder(p);
        if(holder == null) return;
        holder.onLoaded(p);
    }

    public StoryHolder getStoryHolder(Player p){
        return StoryHolder.storyHolder(p);
    }
}
