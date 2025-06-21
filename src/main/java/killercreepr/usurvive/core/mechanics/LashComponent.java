package killercreepr.usurvive.core.mechanics;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.cruxitems.api.item.component.ConsumableComponent;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.consume.ItemConsumeContext;
import killercreepr.cruxitems.api.item.consume.ItemConsumeResult;
import killercreepr.cruxitems.api.item.context.PlayerItemedContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class LashComponent implements InteractableComponent, ConsumableComponent {
    protected final int cooldown;
    protected final byte itemDamage;
    protected final float pushForce;
    protected final float pushForceY;
    protected final CreateSound useSound;

    public LashComponent(int cooldown, byte itemDamage, float pushForce, float pushForceY, CreateSound useSound) {
        this.cooldown = cooldown;
        this.itemDamage = itemDamage;
        this.pushForce = pushForce;
        this.pushForceY = pushForceY;
        this.useSound = useSound;
    }

    public int getCooldown() {
        return cooldown;
    }

    public byte getItemDamage() {
        return itemDamage;
    }

    public float getPushForce() {
        return pushForce;
    }

    public float getPushForceY() {
        return pushForceY;
    }

    public CreateSound getUseSound() {
        return useSound;
    }

    @Override
    public @NotNull ItemUseResult onInteract(@NotNull ItemUseContext ctx) {
        if(ctx.getEntityClicked() != null && ctx.getAction().isLeftClick()) return ItemUseResult.empty();
        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();
        lash(p, item, ctx);
        return ItemUseResult.cancelled();
    }

    public boolean lash(Player p, CruxItem item,
                        PlayerItemedContext ctx){
        Location loc = p.getLocation();
        loc.setPitch(0f);
        Vector look = loc.getDirection();
        look.multiply(pushForce);
        look.setY(pushForceY);
        p.setVelocity(p.getVelocity().add(look));
        p.setFallDistance(p.getFallDistance()*.5f);

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
        if(lash(p, item, ctx)){
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
