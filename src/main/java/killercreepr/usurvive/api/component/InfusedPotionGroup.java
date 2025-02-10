package killercreepr.usurvive.api.component;

import killercreepr.cruxattributes.api.equipment.CruxSlotGroup;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface InfusedPotionGroup {
    @NotNull List<PotionEffect> getPotionEffects();
    @NotNull CruxSlotGroup getSlot();
    @Contract(pure = true)
    InfusedPotionGroup withPotionEffects(@NotNull List<PotionEffect> effects);
}
