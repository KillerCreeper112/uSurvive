package killercreepr.usurvive.core.component.impl;

import killercreepr.usurvive.api.component.WaterProjectile;

public class SimpleWaterProjectile implements WaterProjectile {
    protected final double force;
    protected final double dropOffRate;
    protected final int maxTime;

    public SimpleWaterProjectile(double force, double dropOffRate, int maxTime) {
        this.force = force;
        this.dropOffRate = dropOffRate;
        this.maxTime = maxTime;
    }

    @Override
    public double force() {
        return force;
    }

    @Override
    public double dropOffRate() {
        return dropOffRate;
    }

    @Override
    public int maxTime() {
        return maxTime;
    }
}
