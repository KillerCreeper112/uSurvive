package killercreepr.usurvive.api.guide.tips;

import killercreepr.crux.api.data.CruxKeyed;

import java.util.List;

public interface TipCategory extends CruxKeyed {
    List<Tip> tips();
    Tip randomTip();
}
