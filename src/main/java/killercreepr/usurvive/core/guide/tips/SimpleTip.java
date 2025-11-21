package killercreepr.usurvive.core.guide.tips;

import killercreepr.usurvive.api.guide.tips.Tip;

import java.util.List;

public class SimpleTip implements Tip {
    protected final List<String> message;

    public SimpleTip(List<String> message) {
        this.message = message;
    }

    @Override
    public List<String> message() {
        return message;
    }
}
