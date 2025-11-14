package killercreepr.usurvive.api.story;

import java.util.Collection;
import java.util.Map;

public interface StoryState {
    default boolean hasAllFlags(Collection<StoryFlag> flags){
        for (StoryFlag flag : flags) {
            if(!hasFlag(flag)) return false;
        }
        return true;
    }
    default boolean hasAnyFlags(Collection<StoryFlag> flags){
        for (StoryFlag flag : flags) {
            if(hasFlag(flag)) return true;
        }
        return false;
    }
    boolean hasFlag(StoryFlag flag);
    void setFlag(StoryFlag flag, boolean value);
    default void grantFlag(StoryFlag flag){
        setFlag(flag, true);
    }
    default void revokeFlag(StoryFlag flag){
        setFlag(flag, false);
    }
    Collection<String> getCompletedFlags();
    Map<String, Integer> getIntFlags();


    void setFlagValue(StoryFlag flag, int value);
    default void incrementFlag(StoryFlag flag, int amount){
        setFlagValue(flag, getFlagValue(flag) + amount);
    }
    default void decrementFlag(StoryFlag flag, int amount){
        setFlagValue(flag, getFlagValue(flag) - amount);
    }
    int getFlagValue(StoryFlag flag);
}
