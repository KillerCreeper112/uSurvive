package killercreepr.usurvive.core.guide.config;

import killercreepr.usurvive.core.config.handler.FileGuideProgression;
import killercreepr.usurvive.core.guide.progression.FirstGuideProgression;
import killercreepr.usurvive.core.guide.progression.FourthGuideProgression;
import killercreepr.usurvive.core.guide.progression.SecondGuideProgression;
import killercreepr.usurvive.core.guide.progression.ThirdGuideProgression;

public class GuideCfgLoader {
    protected final FileGuideProgression fileGuideProgression;

    public GuideCfgLoader(FileGuideProgression fileGuideProgression) {
        this.fileGuideProgression = fileGuideProgression;
    }

    public void load(){
        fileGuideProgression.register(FirstGuideProgression.KEY, new FirstGuideProgression.Loader());
        fileGuideProgression.register(SecondGuideProgression.KEY, new SecondGuideProgression.Loader());
        fileGuideProgression.register(ThirdGuideProgression.KEY, new ThirdGuideProgression.Loader());
        fileGuideProgression.register(FourthGuideProgression.KEY, new FourthGuideProgression.Loader());
    }
}
