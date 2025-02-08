package killercreepr.usurvive.core.component.impl;

import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import killercreepr.usurvive.api.component.InfusedPotionGroup;
import net.kyori.adventure.key.Key;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleInfusedPotionGroup implements InfusedPotionGroup {
    protected final Key key;
    protected final List<PotionEffect> potionEffects;
    protected final CruxSlotGroup slot;

    public SimpleInfusedPotionGroup(Key key, List<PotionEffect> potionEffects, CruxSlotGroup slot) {
        this.key = key;
        this.potionEffects = potionEffects;
        this.slot = slot;
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
        return new SimpleInfusedPotionGroup(key, effects, slot);
    }

    @Override
    public @NotNull Key key() {
        return key;
    }
}
