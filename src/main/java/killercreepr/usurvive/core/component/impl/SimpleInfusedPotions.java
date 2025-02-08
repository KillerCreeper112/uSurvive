package killercreepr.usurvive.core.component.impl;

import killercreepr.usurvive.api.component.InfusedPotionGroup;
import killercreepr.usurvive.api.component.InfusedPotions;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleInfusedPotions implements InfusedPotions {
    protected final Map<Key, InfusedPotionGroup> infusedPotions;

    public SimpleInfusedPotions(Map<Key, InfusedPotionGroup> infusedPotions) {
        this.infusedPotions = infusedPotions;
    }

    public SimpleInfusedPotions(Collection<InfusedPotionGroup> infusedPotions) {
        this(new HashMap<>());
        infusedPotions.forEach(p -> this.infusedPotions.put(p.key(), p));
    }

    @NotNull
    @Override
    public Collection<InfusedPotionGroup> getInfusedPotions() {
        return infusedPotions.values();
    }

    @Nullable
    @Override
    public InfusedPotionGroup getInfusedPotion(@NotNull Key key) {
        return infusedPotions.get(key);
    }

    @Override
    public InfusedPotions withInfusedPotions(@NotNull Collection<InfusedPotionGroup> effects) {
        return new SimpleInfusedPotions(effects);
    }
}
