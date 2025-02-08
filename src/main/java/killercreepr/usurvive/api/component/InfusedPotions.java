package killercreepr.usurvive.api.component;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface InfusedPotions {
    @NotNull Collection<InfusedPotionGroup> getInfusedPotions();
    @Nullable InfusedPotionGroup getInfusedPotion(@NotNull Key key);
    @Contract(pure = true)
    InfusedPotions withInfusedPotions(@NotNull Collection<InfusedPotionGroup> effects);
}
