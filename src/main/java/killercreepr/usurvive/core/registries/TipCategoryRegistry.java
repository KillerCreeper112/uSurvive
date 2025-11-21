package killercreepr.usurvive.core.registries;

import killercreepr.crux.core.registry.SimpleKeyedRegistry;
import killercreepr.usurvive.api.guide.tips.Tip;
import killercreepr.usurvive.api.guide.tips.TipCategory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TipCategoryRegistry extends SimpleKeyedRegistry<TipCategory> {
    public Tip randomTip(){
        int total = 0;

        // Count all tips
        for (TipCategory category : this) {
            total += category.tips().size();
        }
        if (total == 0) return null;

        int targetIndex = ThreadLocalRandom.current().nextInt(total);
        int current = 0;

        // Walk until the chosen index falls within a category
        for (TipCategory category : this) {
            List<Tip> list = category.tips();
            if (targetIndex < current + list.size()) {
                return list.get(targetIndex - current);
            }
            current += list.size();
        }

        return null;
    }
}
