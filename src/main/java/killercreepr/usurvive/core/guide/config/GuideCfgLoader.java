package killercreepr.usurvive.core.guide.config;

import killercreepr.usurvive.core.config.handler.FileGuideProgression;
import killercreepr.usurvive.core.guide.progression.*;

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
        fileGuideProgression.register(FifthGuideProgression.KEY, new FifthGuideProgression.Loader());
        fileGuideProgression.register(SixthGuideProgression.KEY, new SixthGuideProgression.Loader());
        fileGuideProgression.register(SeventhGuideProgression.KEY, new SeventhGuideProgression.Loader());
        fileGuideProgression.register(EighthGuideProgression.KEY, new EighthGuideProgression.Loader());
        fileGuideProgression.register(NinthGuideProgression.KEY, new NinthGuideProgression.Loader());
        fileGuideProgression.register(TenthGuideProgression.KEY, new TenthGuideProgression.Loader());
        fileGuideProgression.register(EleventhGuideProgression.KEY, new EleventhGuideProgression.Loader());
        fileGuideProgression.register(TwelfthGuideProgression.KEY, new TwelfthGuideProgression.Loader());
        fileGuideProgression.register(ThirteenthGuideProgression.KEY, new ThirteenthGuideProgression.Loader());
        fileGuideProgression.register(FourteenthGuideProgression.KEY, new FourteenthGuideProgression.Loader());
    }
}
