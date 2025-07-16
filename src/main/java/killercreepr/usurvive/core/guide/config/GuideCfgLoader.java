package killercreepr.usurvive.core.guide.config;

import killercreepr.usurvive.core.config.handler.FileGuideProgression;
import killercreepr.usurvive.core.guide.progression.FirstGuideProgression;
import killercreepr.usurvive.core.guide.progression.SecondGuideProgression;

public class GuideCfgLoader {
    protected final FileGuideProgression fileGuideProgression;

    public GuideCfgLoader(FileGuideProgression fileGuideProgression) {
        this.fileGuideProgression = fileGuideProgression;
    }

    public void load(){
        fileGuideProgression.register(FirstGuideProgression.KEY, new FirstGuideProgression.Loader());
        fileGuideProgression.register(SecondGuideProgression.KEY, new SecondGuideProgression.Loader());
    }
}
