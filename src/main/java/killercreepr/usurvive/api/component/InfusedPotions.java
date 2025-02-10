package killercreepr.usurvive.api.component;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface InfusedPotions {
    @NotNull List<InfusedPotionGroup> getInfusedPotions();
    @Nullable InfusedPotionGroup getInfusedPotion(@NotNull Key key);
    @Contract(pure = true)
    InfusedPotions withInfusedPotions(@NotNull List<InfusedPotionGroup> effects);
}
