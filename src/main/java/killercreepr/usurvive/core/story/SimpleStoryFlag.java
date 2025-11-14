package killercreepr.usurvive.core.story;

import killercreepr.crux.core.data.SimpleKeyed;
import killercreepr.usurvive.api.story.StoryFlag;
import killercreepr.usurvive.api.story.StoryState;
import net.kyori.adventure.key.Key;

import java.util.function.Function;

public class SimpleStoryFlag extends SimpleKeyed implements StoryFlag {
    protected final Function<StoryState, Boolean> isComplete;

    public SimpleStoryFlag(Key key, Function<StoryState, Boolean> isComplete) {
        super(key);
        this.isComplete = isComplete;
    }

    @Override
    public Boolean isComplete(StoryState state) {
        return isComplete == null ? null : isComplete.apply(state);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof StoryFlag f)) return false;
        return f.key().equals(key);
    }

    @Override
    public String toString() {
        return "SimpleStoryFlag{" +
            "key=" + key +
            '}';
    }
}
