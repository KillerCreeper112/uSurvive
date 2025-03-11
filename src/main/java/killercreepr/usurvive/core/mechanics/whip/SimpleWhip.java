package killercreepr.usurvive.core.mechanics.whip;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxitems.api.item.consume.ItemConsumeContext;
import killercreepr.cruxitems.api.item.consume.ItemConsumeResult;
import killercreepr.cruxitems.api.item.context.PlayerItemedContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.usurvive.api.mechanics.whip.ActiveWhip;
import killercreepr.usurvive.api.mechanics.whip.Whip;
import killercreepr.usurvive.api.mechanics.whip.WhipHook;
import killercreepr.usurvive.api.mechanics.whip.context.WhipContext;
import killercreepr.usurvive.core.attribute.USurviveAttribute;
import killercreepr.usurvive.core.data.entity.WhipperHolder;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleWhip implements Whip {
    protected final @NotNull Key whipType;
    protected final int maxWhips;
    protected final int cooldown;
    protected final CreateSound throwSound;

    public SimpleWhip(@NotNull Key whipType, int maxWhips, int cooldown, CreateSound throwSound) {
        this.whipType = whipType;
        this.maxWhips = maxWhips;
        this.cooldown = cooldown;
        this.throwSound = throwSound;
    }

    @Override
    public int getMaxWhips() {
        return maxWhips;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @NotNull
    @Override
    public Key getWhipType() {
        return whipType;
    }

    @Nullable
    @Override
    public CreateSound getThrowSound() {
        return throwSound;
    }

    public WhipContext buildWhipContext(PlayerItemedContext ctx, int id){
        Player p = ctx.getPlayer();
        return WhipContext.context(p.getEyeLocation(), p, id,
            CruxAttribute.get(p, USurviveAttribute.WHIP_THROW_FORCE),
            (int) CruxAttribute.get(p, USurviveAttribute.WHIP_PIERCE));
    }

    @Override
    public @NotNull ItemUseResult onInteract(@NotNull ItemUseContext ctx) {
        if(ctx.getEntityClicked() != null && ctx.getAction().isLeftClick()) return ItemUseResult.empty();
        WhipHook hook = USurviveRegistries.WHIP_HOOK.get(getWhipType());
        if(hook==null || maxWhips < 1) return ItemUseResult.empty();
        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();
        throwWhip(hook, p, item, ctx);
        return ItemUseResult.cancelled();
    }

    public boolean throwWhip(WhipHook hook, Player p, CruxItem item,
                             PlayerItemedContext ctx){
        WhipperHolder whipper = PlayerMemory.getOrCreate(p)
            .getPlayerDataHolderOrCompute(WhipperHolder.class, WhipperHolder::new);

        int id;
        if(maxWhips > 1 && ctx instanceof ItemUseContext c){
            id = c.getAction().isRightClick() ? 0 : 1;
        } else id = 0;

        ActiveWhip previous = whipper.getWhip(id);
        if(previous != null){
            previous.remove(p);
            return true;
        }
        if(p.hasCooldown(item.item())){
            return false;
        }
        if(cooldown > 0) p.setCooldown(item.item(), cooldown);
        whipper.throwNewHook(hook, buildWhipContext(ctx, id));
        if(throwSound != null) throwSound.playAt(p.getLocation());
        if(p.getGameMode() != GameMode.CREATIVE){
            Crux.handlers().item().damageItem(item.item(), 1, p);
        }
        return true;
    }

    @Override
    public boolean isInteractable(@NotNull ItemUseContext ctx) {
        if(ctx.getAction() == Action.PHYSICAL) return false;
        return true;
    }

    @Override
    public @NotNull ItemConsumeResult onConsume(@NotNull ItemConsumeContext ctx) {
        WhipHook hook = USurviveRegistries.WHIP_HOOK.get(getWhipType());
        if(hook==null || maxWhips < 1) return ItemConsumeResult.cancelled();
        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();
        throwWhip(hook, p, item, ctx);
        return ItemConsumeResult.cancelled();
    }

    @Override
    public boolean isConsumable(@NotNull ItemConsumeContext ctx) {
        return true;
    }
}
