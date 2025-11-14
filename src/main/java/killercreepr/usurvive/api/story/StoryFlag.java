package killercreepr.usurvive.api.story;

import killercreepr.crux.api.data.CruxKeyed;
import killercreepr.crux.core.Crux;
import killercreepr.usurvive.core.story.SimpleStoryFlag;

import java.util.function.Function;

public interface StoryFlag extends CruxKeyed {
    static StoryFlag f(String id){
        return f(id, null);
    }
    static StoryFlag f(String id, Function<StoryState, Boolean> isComplete){
        return new SimpleStoryFlag(Crux.key(id), isComplete);
    }
    default Boolean isComplete(StoryState state){
        return null;
    }
}
