package killercreepr.usurvive.api.mechanics.whip;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.cruxitems.api.item.component.ConsumableComponent;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Whip extends InteractableComponent, ConsumableComponent {
    int getMaxWhips();
    int getCooldown();
    int getItemDamage();
    @NotNull Key getWhipType();
    @Nullable CreateSound getThrowSound();
}
