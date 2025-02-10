package killercreepr.usurvive.core.component.impl;

import killercreepr.usurvive.api.component.PotionInfusable;

public class SimplePotionInfusable implements PotionInfusable {
    protected final int maxInfusions;

    public SimplePotionInfusable(int maxInfusions) {
        this.maxInfusions = maxInfusions;
    }

    @Override
    public int getMaxInfusions() {
        return maxInfusions;
    }
}
