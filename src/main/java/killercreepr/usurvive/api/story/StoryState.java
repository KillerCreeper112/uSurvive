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

    /**
     * @return Whether the state has changed
     */
    boolean setFlag(StoryFlag flag, boolean value);
    /**
     * @return Whether the state has changed
     */
    default boolean grantFlag(StoryFlag flag){
        return setFlag(flag, true);
    }
    /**
     * @return Whether the state has changed
     */
    default boolean revokeFlag(StoryFlag flag){
        return setFlag(flag, false);
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
