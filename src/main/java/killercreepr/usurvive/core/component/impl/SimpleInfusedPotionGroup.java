package killercreepr.usurvive.core.component.impl;

import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import killercreepr.usurvive.api.component.InfusedPotionGroup;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class SimpleInfusedPotionGroup implements InfusedPotionGroup {
    protected final List<PotionEffect> potionEffects;
    protected final CruxSlotGroup slot;

    public SimpleInfusedPotionGroup(List<PotionEffect> potionEffects, CruxSlotGroup slot) {
        this.potionEffects = potionEffects;
        this.slot = slot;
    }

    @Override
    public String toString() {
        return "SimpleInfusedPotionGroup{" +
            "potionEffects=" + potionEffects +
            ", slot=" + slot +
            '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof SimpleInfusedPotionGroup g)) return false;
        return g.potionEffects.equals(potionEffects) && slot.equals(g.slot);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(potionEffects) + Objects.hashCode(slot);
    }

    @NotNull
    @Override
    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    @NotNull
    @Override
    public CruxSlotGroup getSlot() {
        return slot;
    }

    @Override
    public InfusedPotionGroup withPotionEffects(@NotNull List<PotionEffect> effects) {
        return new SimpleInfusedPotionGroup(effects, slot);
    }
}
