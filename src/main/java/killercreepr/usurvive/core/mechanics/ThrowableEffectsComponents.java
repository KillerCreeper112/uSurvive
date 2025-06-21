package killercreepr.usurvive.core.mechanics;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.cruxpotions.api.potion.StoredPotion;
import killercreepr.cruxpotions.core.component.PotionComponents;
import killercreepr.usurvive.core.component.USurviveComponents;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class ThrowableEffectsComponents implements InteractableComponent {
    protected final List<StoredPotion> cruxPotions;
    protected final List<PotionEffect> potions;
    protected final CreateSound throwSound;
    protected final float throwForce;
    protected final float spread;

    public ThrowableEffectsComponents(List<StoredPotion> cruxPotions, List<PotionEffect> potions, CreateSound throwSound, float throwForce, float spread) {
        this.cruxPotions = cruxPotions;
        this.potions = potions;
        this.throwSound = throwSound;
        this.throwForce = throwForce;
        this.spread = spread;
    }

    public List<StoredPotion> getCruxPotions() {
        return cruxPotions;
    }

    public List<PotionEffect> getPotions() {
        return potions;
    }

    public CreateSound getThrowSound() {
        return throwSound;
    }

    public float getSpread() {
        return spread;
    }

    public float getThrowForce() {
        return throwForce;
    }

    public Vector getRandomizedDirection(Vector direction, float spread, Random random) {
        // Normalize the direction just in case
        direction = direction.normalize();

        // Add small random variations to each component
        double dx = direction.getX() + (random.nextGaussian() * spread);
        double dy = direction.getY() + (random.nextGaussian() * spread);
        double dz = direction.getZ() + (random.nextGaussian() * spread);

        return new Vector(dx, dy, dz).normalize();
    }


    @Override
    public @NotNull ItemUseResult onInteract(@NotNull ItemUseContext ctx) {
        if(ctx.getEntityClicked() != null && ctx.getAction().isLeftClick()) return ItemUseResult.empty();
        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();

        ItemStack clone = item.item().clone();
        clone.setAmount(1);

        if(p.getGameMode() != GameMode.CREATIVE){
            item.amount(item.amount()-1);
        }

        Vector dir = getRandomizedDirection(
            p.getEyeLocation().getDirection(),
            spread,
            CruxMath.random()
        ).multiply(throwForce);

        p.launchProjectile(Snowball.class, dir, e ->{
            e.setItem(clone);
            CruxEntity crux = CruxEntity.entity(e);
            crux.set(PotionComponents.STORED_CRUX_POTIONS, cruxPotions);
            crux.set(USurviveComponents.PROJECTILE_HIT_POTIONS, potions);

            var attributes = item.getOrDefaultData(USurviveComponents.PROJECTILE_ATTRIBUTES);
            if(attributes != null){
                attributes.getAttributeInstances().forEach(instance ->{
                    instance.getModifiers().forEach(mod ->{
                        CruxAttribute.addModifier(e, instance.getAttribute(), mod);
                    });
                });
            }
        });

        if(throwSound != null) throwSound.playAt(p);

        p.swingHand(ctx.getHand());

        return ItemUseResult.cancelled();
    }

    @Override
    public boolean isInteractable(@NotNull ItemUseContext ctx) {
        return ctx.getAction().isRightClick();
    }
}
