package killercreepr.usurvive.core.component.impl;

import killercreepr.usurvive.api.component.InfusedPotionGroup;
import killercreepr.usurvive.api.component.InfusedPotions;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleInfusedPotions implements InfusedPotions {
    //protected final Map<Key, InfusedPotionGroup> infusedPotions;
    protected final List<InfusedPotionGroup> infusedPotions;

    public SimpleInfusedPotions(List<InfusedPotionGroup> infusedPotions) {
        this.infusedPotions = infusedPotions;
    }

    /*public SimpleInfusedPotions(Map<Key, InfusedPotionGroup> infusedPotions) {
        this.infusedPotions = infusedPotions;
    }

    public SimpleInfusedPotions(Collection<InfusedPotionGroup> infusedPotions) {
        this(new HashMap<>());
        infusedPotions.forEach(p -> this.infusedPotions.put(p.key(), p));
    }*/

    @NotNull
    @Override
    public List<InfusedPotionGroup> getInfusedPotions() {
        return infusedPotions;
    }

    @Nullable
    @Override
    public InfusedPotionGroup getInfusedPotion(@NotNull Key key) {
        return null;//infusedPotions.get(key);
    }

    @Override
    public InfusedPotions withInfusedPotions(@NotNull List<InfusedPotionGroup> effects) {
        return new SimpleInfusedPotions(effects);
    }
}
