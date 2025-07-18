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
        fileGuideProgression.register(FifteenthGuideProgression.KEY, new FifteenthGuideProgression.Loader());
        fileGuideProgression.register(SixteenthGuideProgression.KEY, new SixteenthGuideProgression.Loader());
        fileGuideProgression.register(SeventeenthGuideProgression.KEY, new SeventeenthGuideProgression.Loader());
        fileGuideProgression.register(EighteenthGuideProgression.KEY, new EighteenthGuideProgression.Loader());
        fileGuideProgression.register(NineteenthGuideProgression.KEY, new NineteenthGuideProgression.Loader());
        fileGuideProgression.register(Guide20Progression.KEY, new Guide20Progression.Loader());
        fileGuideProgression.register(Guide21Progression.KEY, new Guide21Progression.Loader());
        fileGuideProgression.register(Guide22Progression.KEY, new Guide22Progression.Loader());
        fileGuideProgression.register(Guide23Progression.KEY, new Guide23Progression.Loader());
    }
}
