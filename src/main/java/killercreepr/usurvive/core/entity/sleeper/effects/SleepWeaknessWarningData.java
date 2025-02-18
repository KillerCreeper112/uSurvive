package killercreepr.usurvive.core.entity.sleeper.effects;

import killercreepr.crux.api.communication.Communicator;

public class SleepWeaknessWarningData {
    protected final Communicator msg;
    protected final int minSlumberTicks;
    protected final int maxSlumberTicks;

    public SleepWeaknessWarningData(Communicator msg, int minSlumberTicks, int maxSlumberTicks) {
        this.msg = msg;
        this.minSlumberTicks = minSlumberTicks;
        this.maxSlumberTicks = maxSlumberTicks;
    }

    public Communicator getMsg() {
        return msg;
    }

    public int getMinSlumberTicks() {
        return minSlumberTicks;
    }

    public int getMaxSlumberTicks() {
        return maxSlumberTicks;
    }
}
