package killercreepr.usurvive.core.mechanics;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.cruxpotions.api.potion.StoredPotion;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.entity.memory.StickyChargeMemory;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StickyChargeComponent implements InteractableComponent {
    protected final float throwForce;
    protected final CruxAttributeContainer attributes;
    protected final int fuseTicks;
    protected final List<StoredPotion> cruxPotions;

    public StickyChargeComponent(float throwForce, CruxAttributeContainer attributes, int fuseTicks, List<StoredPotion> cruxPotions) {
        this.throwForce = throwForce;
        this.attributes = attributes;
        this.fuseTicks = fuseTicks;
        this.cruxPotions = cruxPotions;
    }

    public List<StoredPotion> getCruxPotions() {
        return cruxPotions;
    }

    public float getThrowForce() {
        return throwForce;
    }

    public CruxAttributeContainer getAttributes() {
        return attributes;
    }

    public int getFuseTicks() {
        return fuseTicks;
    }

    @Override
    public @NotNull ItemUseResult onInteract(@NotNull ItemUseContext ctx) {
        var p = ctx.getPlayer();
        ItemStack item = ctx.getItem().item();
        if(p.getGameMode() != GameMode.CREATIVE) item.setAmount(item.getAmount()-1);

        USurviveMobs.STICKY_CHARGE.spawn(p.getEyeLocation(), e ->{
            if(attributes != null){
                attributes.getAttributeInstances().forEach(instance ->{
                    instance.getModifiers().forEach(mod ->{
                        CruxAttribute.addModifier(e, instance.getAttribute(), mod);
                    });
                });
            }

            CruxTag.set(e, "fuse_ticks", PersistentDataType.INTEGER, fuseTicks);

            if(EntityMemory.get(e) instanceof StickyChargeMemory m){
                m.setFuseTicks(fuseTicks);
                m.setData(this);
            }

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
