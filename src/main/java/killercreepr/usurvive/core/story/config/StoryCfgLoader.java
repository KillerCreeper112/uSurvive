package killercreepr.usurvive.core.story.config;

import killercreepr.usurvive.core.config.handler.FileStoryProgression;
import killercreepr.usurvive.core.guide.progression.*;
import killercreepr.usurvive.core.story.progression.Story1Progress;

public class StoryCfgLoader {
    protected final FileStoryProgression fileStoryProgression;

    public StoryCfgLoader(FileStoryProgression fileStoryProgression) {
        this.fileStoryProgression = fileStoryProgression;
    }

    public void load(){
        fileStoryProgression.register(Story1Progress.KEY, new Story1Progress.Loader());
    }
}
