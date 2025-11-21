package killercreepr.usurvive.core.guide.tips;

import killercreepr.crux.core.data.SimpleKeyed;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.usurvive.api.guide.tips.Tip;
import killercreepr.usurvive.api.guide.tips.TipCategory;
import net.kyori.adventure.key.Key;

import java.util.List;

public class SimpleTipCategory extends SimpleKeyed implements TipCategory {
    protected final List<Tip> tips;
    public SimpleTipCategory(Key key, List<Tip> tips) {
        super(key);
        this.tips = tips;
    }

    @Override
    public List<Tip> tips() {
        return tips;
    }

    @Override
    public Tip randomTip() {
        if(tips.isEmpty()) throw new IllegalStateException("Tips cannot be empty! SimpleTipCategory");
        return CruxCollection.getRandom(tips);
    }
}
