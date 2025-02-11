package killercreepr.usurvive.core.component.impl;

import net.kyori.adventure.key.Key;

import java.util.Map;

public class InfusionAttributesBenefit {
    protected final Map<Key, Double> attributes;
    protected final boolean keepDefaults;

    public InfusionAttributesBenefit(Map<Key, Double> attributes, boolean keepDefaults) {
        this.attributes = attributes;
        this.keepDefaults = keepDefaults;
    }

    public Map<Key, Double> getAttributes() {
        return attributes;
    }

    public boolean isKeepDefaults() {
        return keepDefaults;
    }
}
