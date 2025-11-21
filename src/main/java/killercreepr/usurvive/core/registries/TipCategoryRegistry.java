package killercreepr.usurvive.core.registries;

import killercreepr.crux.core.registry.SimpleKeyedRegistry;
import killercreepr.usurvive.api.guide.tips.Tip;
import killercreepr.usurvive.api.guide.tips.TipCategory;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public Tip randomTipSkewed(@NotNull Key skewedCategory, double skewAmount){
        return randomTipSkewed(get(skewedCategory), skewAmount);
    }

    /**
     *
     * @param skewedCategory If null, returns randomTip();
     */
    public Tip randomTipSkewed(@Nullable TipCategory skewedCategory, double skewAmount) {
        if(skewedCategory == null) return randomTip();
        if (skewAmount <= 0) skewAmount = 0.00001; // prevent degenerate cases

        // 1. Count total weighted tips
        double totalWeight = 0;

        for (TipCategory category : this) {
            int size = category.tips().size();
            if (category == skewedCategory) {
                totalWeight += size * skewAmount;
            } else {
                totalWeight += size;
            }
        }

        if (totalWeight <= 0) return null;

        // 2. Pick a random target in [0, totalWeight)
        double target = ThreadLocalRandom.current().nextDouble(totalWeight);

        // 3. Walk through categories until we hit the selected weight range
        double cumulative = 0;

        for (TipCategory category : this) {
            List<Tip> tips = category.tips();
            double weight = (category == skewedCategory ? skewAmount : 1.0) * tips.size();

            if (target < cumulative + weight) {
                // Select a tip uniformly *within* that category
                return tips.get(ThreadLocalRandom.current().nextInt(tips.size()));
            }

            cumulative += weight;
        }

        return null; // unreachable
    }

}
