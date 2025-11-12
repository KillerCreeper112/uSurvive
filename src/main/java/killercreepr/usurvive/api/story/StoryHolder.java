package killercreepr.usurvive.api.story;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.usurvive.core.story.SimpleStoryHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

public interface StoryHolder {
    static StoryHolder storyHolder(Entity e){
        return EntityMemory.getOrCreateDataHolder(e, SimpleStoryHolder.class, mem ->{
            if(mem instanceof PlayerMemory m) return new SimpleStoryHolder(m);
            return null;
        });
    }
    default void addProgression(StoryProgression progression){
        addProgression(progression, false);
    }

    void addProgression(StoryProgression progression, boolean silent);

    void onLoaded(Player p);

    void complete(StoryProgression progression);

    Collection<StoryProgression> getCurrentStoryProgression();
    Map<Key, StoryProgressData> getStoryData();
    StoryProgressData getStoryProgressData(Key key);
    StoryProgression getStoryProgression(Key key);
    boolean hasCompleted(Key storyKey);
    Player player();
}
