package killercreepr.usurvive.core.component.impl;

import org.bukkit.Color;

public class BaseItemUpgradedFrom {
    protected final String type;
    protected final Color color;

    public BaseItemUpgradedFrom(String type, Color color) {
        this.type = type;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }
}
