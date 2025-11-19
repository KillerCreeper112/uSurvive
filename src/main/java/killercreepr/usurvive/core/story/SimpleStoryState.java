package killercreepr.usurvive.core.story;

import killercreepr.usurvive.api.story.StoryFlag;
import killercreepr.usurvive.api.story.StoryState;

import java.util.*;

public class SimpleStoryState implements StoryState {
    protected final Set<String> completedFlags = new HashSet<>();
    protected final Map<String, Integer> intFlags = new HashMap<>();

    protected String f(StoryFlag f){
        return f.key().asString();
    }

    @Override
    public boolean hasFlag(StoryFlag flag) {
        var manual = flag.isComplete(this);
        if(manual != null) return manual;
        return completedFlags.contains(f(flag));
    }

    public void onFlagStateChanged(StoryFlag flag, boolean value){

    }

    public void onFlagValueChanged(StoryFlag flag, int oldValue, int value){

    }

    @Override
    public boolean setFlag(StoryFlag flag, boolean value) {
        if(value){
            if(completedFlags.add(f(flag))){
                onFlagStateChanged(flag, true);
                return true;
            }
        }else{
            if(completedFlags.remove(f(flag))){
                onFlagStateChanged(flag, false);
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<String> getCompletedFlags() {
        return completedFlags;
    }

    @Override
    public Map<String, Integer> getIntFlags() {
        return intFlags;
    }

    @Override
    public void setFlagValue(StoryFlag flag, int value) {
        if(value < 1){
            var removed = intFlags.remove(f(flag));
            if(removed != null){
                onFlagValueChanged(flag, removed, 0);
            }
        }else{
            var changed = intFlags.put(f(flag), value);
            if(!Objects.equals(changed, value)){
                onFlagValueChanged(flag, changed == null ? 0 : changed, value);
            }
        }
    }

    @Override
    public int getFlagValue(StoryFlag flag) {
        return intFlags.getOrDefault(f(flag), 0);
    }
}
