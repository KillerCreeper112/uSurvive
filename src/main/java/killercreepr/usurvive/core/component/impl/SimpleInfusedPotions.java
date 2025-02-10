package killercreepr.usurvive.core.component.impl;

import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import killercreepr.usurvive.api.component.InfusedPotionGroup;
import killercreepr.usurvive.api.component.InfusedPotions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

    @NotNull
    @Override
    public List<InfusedPotionGroup> getApplicableInfusedPotions(@NotNull Predicate<InfusedPotionGroup> filter) {
        List<InfusedPotionGroup> list = new ArrayList<>();
        for(InfusedPotionGroup group : getInfusedPotions()){
            if(filter.test(group)) list.add(group);
        }
        return list;
    }

    @Override
    public InfusedPotions applyToFirstFound(@NotNull Predicate<InfusedPotionGroup> filter,
                                            @NotNull Function<InfusedPotionGroup, InfusedPotionGroup> found,
                                            @Nullable Supplier<InfusedPotionGroup> fallback) {
        List<InfusedPotionGroup> list = new ArrayList<>(infusedPotions);
        int index = -1;
        boolean success = false;
        for(InfusedPotionGroup group : infusedPotions){
            index++;
            if(filter.test(group)){
                InfusedPotionGroup result = found.apply(group);
                if(result == null) list.remove(index);
                else list.set(index, result);
                success = true;
                break;
            }
        }
        if(!success && fallback != null){
            InfusedPotionGroup fall = fallback.get();
            if(fall != null) list.add(fall);
        }
        return withInfusedPotions(list);
    }
}
