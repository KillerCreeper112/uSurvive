package killercreepr.usurvive.api.component;

import killercreepr.cruxattributes.api.equipment.CruxSlot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface InfusedPotions {
    @NotNull List<InfusedPotionGroup> getInfusedPotions();
    @Contract(pure = true)
    InfusedPotions withInfusedPotions(@NotNull List<InfusedPotionGroup> effects);
    @NotNull List<InfusedPotionGroup> getApplicableInfusedPotions(@NotNull CruxSlot... slots);
}
