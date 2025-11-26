package killercreepr.usurvive.api.story;

import killercreepr.crux.api.data.CruxKeyed;

public interface StoryProgressData extends CruxKeyed {
    Long completedTime();
    void complete();

    default boolean isComplete(){
        return completedTime() != null;
    }
    default boolean shouldSave(){
        return true;
    }
}
