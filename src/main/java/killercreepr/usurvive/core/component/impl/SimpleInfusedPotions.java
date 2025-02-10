package killercreepr.usurvive.core.component.impl;

import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.usurvive.api.component.InfusedPotionGroup;
import killercreepr.usurvive.api.component.InfusedPotions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleInfusedPotions implements InfusedPotions {
    //protected final Map<Key, InfusedPotionGroup> infusedPotions;
    protected final List<InfusedPotionGroup> infusedPotions;

    public SimpleInfusedPotions(List<InfusedPotionGroup> infusedPotions) {
        this.infusedPotions = infusedPotions;
    }

    @Override
    public String toString() {
        return "SimpleInfusedPotions{" +
            "infusedPotions=" + infusedPotions +
            '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof SimpleInfusedPotions p)) return false;
        return p.infusedPotions.equals(infusedPotions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(infusedPotions);
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

    @Override
    public InfusedPotions withInfusedPotions(@NotNull List<InfusedPotionGroup> effects) {
        return new SimpleInfusedPotions(effects);
    }

    @NotNull
    @Override
    public List<InfusedPotionGroup> getApplicableInfusedPotions(@NotNull CruxSlot... slots) {
        if(slots.length < 1) return getInfusedPotions();
        List<InfusedPotionGroup> list = new ArrayList<>();
        for(InfusedPotionGroup group : getInfusedPotions()){
            if(group.getSlot().anyMatch(slots)) list.add(group);
        }
        return list;
    }
}
