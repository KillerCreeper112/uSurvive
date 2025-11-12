package killercreepr.usurvive.api.story;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

public interface StoryHolder {
    Collection<StoryProgression> getCurrentStoryProgression();
    Map<Key, StoryProgressData> getStoryProgressData();
    StoryProgressData getStoryProgressData(Key key);
    StoryProgression getStoryProgression(Key key);
    boolean hasCompleted(Key storyKey);
    Player player();
}
