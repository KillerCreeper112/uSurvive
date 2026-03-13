package killercreepr.usurvive.core.story.config;

import killercreepr.usurvive.core.config.handler.FileStoryProgression;
import killercreepr.usurvive.core.story.progression.*;

@Deprecated
public class StoryCfgLoader {
    protected final FileStoryProgression fileStoryProgression;

    public StoryCfgLoader(FileStoryProgression fileStoryProgression) {
        this.fileStoryProgression = fileStoryProgression;
    }

    public void load(){
        fileStoryProgression.register(Story1Progress.KEY, new Story1Progress.Loader());
        fileStoryProgression.register(Story2Progress.KEY, new Story2Progress.Loader());
        fileStoryProgression.register(Story3Progress.KEY, new Story3Progress.Loader());
        fileStoryProgression.register(Story4Progress.KEY, new Story4Progress.Loader());
        fileStoryProgression.register(Story5Progress.KEY, new Story5Progress.Loader());
    }
}
