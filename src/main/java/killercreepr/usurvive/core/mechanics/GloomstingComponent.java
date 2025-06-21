package killercreepr.usurvive.core.mechanics;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxitems.api.item.component.ConsumableComponent;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.consume.ItemConsumeContext;
import killercreepr.cruxitems.api.item.consume.ItemConsumeResult;
import killercreepr.cruxitems.api.item.context.PlayerItemedContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.cruxpotions.api.potion.StoredPotion;
import killercreepr.cruxpotions.core.component.PotionComponents;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.entity.USurviveMobs;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GloomstingComponent implements InteractableComponent, ConsumableComponent {
    protected final int cooldown;

    public List<PotionEffect> getPotions() {
        return potions;
    }

    public List<StoredPotion> getCruxPotions() {
        return cruxPotions;
    }

    protected final int itemDamage;
    protected final float shootForce;
    protected final NumberProvider shootAmount;
    protected final CruxAttributeContainer attributes;
    protected final CreateSound useSound;
    protected final List<StoredPotion> cruxPotions;
    protected final List<PotionEffect> potions;

    public GloomstingComponent(int cooldown, int itemDamage, float shootForce, NumberProvider shootAmount, CruxAttributeContainer attributes, CreateSound useSound, List<StoredPotion> cruxPotions, List<PotionEffect> potions) {
        this.cooldown = cooldown;
        this.itemDamage = itemDamage;
        this.shootForce = shootForce;
        this.shootAmount = shootAmount;
        this.attributes = attributes;
        this.useSound = useSound;
        this.cruxPotions = cruxPotions;
        this.potions = potions;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getItemDamage() {
        return itemDamage;
    }

    public float getShootForce() {
        return shootForce;
    }

    public NumberProvider getShootAmount() {
        return shootAmount;
    }

    public CruxAttributeContainer getAttributes() {
        return attributes;
    }

    public CreateSound getUseSound() {
        return useSound;
    }

    @Override
    public @NotNull ItemUseResult onInteract(@NotNull ItemUseContext ctx) {
        if(ctx.getEntityClicked() != null && ctx.getAction().isLeftClick()) return ItemUseResult.empty();
        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();
        spit(p, item, ctx);
        return ItemUseResult.cancelled();
    }

    public Vector generateDirection(Vector dir){
        dir = dir.clone()
            .rotateAroundX(CruxMath.random(-.1f, .1f))
            .rotateAroundY(CruxMath.random(-.1f, .1f))
            .rotateAroundZ(CruxMath.random(-.1f, .1f));
        return dir;
    }

    public boolean spit(Player p, CruxItem item,
                        PlayerItemedContext ctx){

        int amount = shootAmount.value().intValue();

        Vector dir = p.getEyeLocation().getDirection();
        while(amount > 0){
            amount--;
            USurviveMobs.GLOOMSTING_PROJECTILE.spawn(p.getEyeLocation(), e ->{
                if(attributes != null){
                    attributes.getAttributeInstances().forEach(instance ->{
                        instance.getModifiers().forEach(mod ->{
                            CruxAttribute.addModifier(e, instance.getAttribute(), mod);
                        });
                    });
                }

                CruxEntity crux = CruxEntity.entity(e);
                crux.set(PotionComponents.STORED_CRUX_POTIONS, cruxPotions);
                crux.set(USurviveComponents.PROJECTILE_HIT_POTIONS, potions);

                Vector v = generateDirection(dir)
                    .multiply(shootForce);
                e.setVelocity(v);
            });
        }

        if (useSound != null) useSound.playAt(p);
        return true;
    }

    @Override
    public boolean isInteractable(@NotNull ItemUseContext ctx) {
        if(ctx.getAction() == Action.PHYSICAL) return false;
        CruxItem item = ctx.getItem();
        if(item.item().getData(DataComponentTypes.CONSUMABLE) != null) return false;
        return true;
    }

    @Override
    public @NotNull ItemConsumeResult onConsume(@NotNull ItemConsumeContext ctx) {
        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();
        if(spit(p, item, ctx)){
            if(p.getGameMode() != GameMode.CREATIVE){
                p.damageItemStack(ctx.getSlot(), itemDamage);
                if(cooldown > 0) p.setCooldown(item.item(), cooldown);
            }
        }
        Crux.scheduler().runTask(() ->p.setActiveItemRemainingTime(0));
        return ItemConsumeResult.cancelled();
    }

    @Override
    public boolean isConsumable(@NotNull ItemConsumeContext ctx) {
        return true;
    }
}
