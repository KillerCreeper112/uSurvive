package killercreepr.usurvive.core.mechanics;

import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.usurvive.core.entity.USurviveMobs;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class StickyChargeComponent implements InteractableComponent {
    protected final float throwForce;

    public StickyChargeComponent(float throwForce) {
        this.throwForce = throwForce;
    }

    public float getThrowForce() {
        return throwForce;
    }

    @Override
    public @NotNull ItemUseResult onInteract(@NotNull ItemUseContext ctx) {
        var p = ctx.getPlayer();
        ItemStack item = ctx.getItem().item();
        if(p.getGameMode() != GameMode.CREATIVE) item.setAmount(item.getAmount()-1);

        USurviveMobs.STICKY_CHARGE.spawn(p.getEyeLocation(), e ->{
            Vector dir = p.getEyeLocation().getDirection()
                .multiply(throwForce);
            e.setVelocity(dir);
        });
        p.swingHand(ctx.getHand());
        return ItemUseResult.cancelled();
    }

    @Override
    public boolean isInteractable(@NotNull ItemUseContext ctx) {
        return ctx.getAction().isRightClick();
    }
}
