package killercreepr.usurvive.core.story.progression.data;

import killercreepr.usurvive.api.story.StoryProgressData;

public abstract class GenericStoryProgressData implements StoryProgressData {
    protected Long completedTime;

    @Override
    public Long completedTime() {
        return completedTime;
    }

    @Override
    public void complete() {
        this.completedTime = System.currentTimeMillis();
    }
}
