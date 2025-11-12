package killercreepr.usurvive.core.story.progression.data;

import killercreepr.usurvive.api.story.StoryProgressData;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class GenericKeyedStoryProgressData implements StoryProgressData {
    protected final Key key;
    protected Long completedTime;

    public GenericKeyedStoryProgressData(Key key) {
        this.key = key;
    }

    @Override
    public Long completedTime() {
        return completedTime;
    }

    @Override
    public void complete() {
        this.completedTime = System.currentTimeMillis();
    }

    @Override
    public @NotNull Key key() {
        return key;
    }
}
