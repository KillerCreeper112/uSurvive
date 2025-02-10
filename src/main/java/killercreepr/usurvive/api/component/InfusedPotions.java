package killercreepr.usurvive.api.component;

import killercreepr.cruxattributes.api.equipment.CruxSlot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface InfusedPotions {
    @NotNull List<InfusedPotionGroup> getInfusedPotions();
    @Contract(pure = true)
    InfusedPotions withInfusedPotions(@NotNull List<InfusedPotionGroup> effects);
    @NotNull List<InfusedPotionGroup> getApplicableInfusedPotions(@NotNull CruxSlot... slots);
    @NotNull List<InfusedPotionGroup> getApplicableInfusedPotions(@NotNull Predicate<InfusedPotionGroup> filter);

    @Contract(pure = true)
    InfusedPotions applyToFirstFound(@NotNull Predicate<InfusedPotionGroup> filter, @NotNull Function<InfusedPotionGroup, InfusedPotionGroup> found,
                                     @Nullable Supplier<InfusedPotionGroup> fallback);
}
