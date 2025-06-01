package killercreepr.usurvive.core.mechanics.grapple;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.usurvive.api.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.api.mechanics.grapple.Grapple;
import killercreepr.usurvive.api.mechanics.grapple.GrappleHook;
import killercreepr.usurvive.api.mechanics.grapple.context.GrappleContext;
import killercreepr.usurvive.core.data.entity.GrapplerHolder;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import net.kyori.adventure.key.Key;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;

public class SimpleGrapple implements Grapple {
    protected final @NotNull Key hook;
    protected final int maxHooks;
    protected final int cooldown;
    protected final float pullForce;
    protected final float throwForce;
    protected final float pullForceDecayRate;
    protected final float minPullForceMultiplier;
    protected final int decayDuration;
    protected final float swingBoostSpeed;
    protected final float ropeBoostSpeed;
    protected final int itemCountUse;
    protected final float swapHandsClimbSpeed;

    public SimpleGrapple(@NotNull Key hook, int maxHooks, int cooldown, float pullForce, float throwForce,
                         float pullForceDecayRate, float minPullForceMultiplier, int decayDuration, float swingBoostSpeed,
                         float ropeBoostSpeed, int itemCountUse, float swapHandsClimbSpeed) {
        this.hook = hook;
        this.maxHooks = maxHooks;
        this.cooldown = cooldown;
        this.pullForce = pullForce;
        this.throwForce = throwForce;
        this.pullForceDecayRate = pullForceDecayRate;
        this.minPullForceMultiplier = minPullForceMultiplier;
        this.decayDuration = decayDuration;
        this.swingBoostSpeed = swingBoostSpeed;
        this.ropeBoostSpeed = ropeBoostSpeed;
        this.itemCountUse = itemCountUse;
        this.swapHandsClimbSpeed = swapHandsClimbSpeed;
    }

    public int getMaxHooks() {
        return maxHooks;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public float getPullForce() {
        return pullForce;
    }

    @Override
    public float getThrowForce() {
        return throwForce;
    }

    @Override
    public float getPullForceDecayRate() {
        return pullForceDecayRate;
    }

    @Override
    public float getMinPullForceMultiplier() {
        return minPullForceMultiplier;
    }

    @Override
    public int getDecayDuration() {
        return decayDuration;
    }

    @Override
    public float getRopeBoostSpeed() {
        return ropeBoostSpeed;
    }

    @Override
    public float getSwingBoostSpeed() {
        return swingBoostSpeed;
    }

    @Override
    public int getItemCountUse() {
        return itemCountUse;
    }

    @Override
    public boolean hasSwapHandsClimb() {
        return swapHandsClimbSpeed > 0f;
    }

    @Override
    public float getSwapHandsClimbSpeed() {
        return swapHandsClimbSpeed;
    }

    @NotNull
    public Key getHookType() {
        return hook;
    }

    public GrappleContext buildGrappleContext(ItemUseContext ctx, int id){
        Player p = ctx.getPlayer();
        return GrappleContext.context(p.getEyeLocation(), p, id, pullForce, throwForce,
            pullForceDecayRate, minPullForceMultiplier, decayDuration, swingBoostSpeed, ropeBoostSpeed,
            swapHandsClimbSpeed);
    }

    @Override
    public @NotNull ItemUseResult onInteract(@NotNull ItemUseContext ctx) {
        if(ctx.getEntityClicked() != null && ctx.getAction().isLeftClick()) return ItemUseResult.empty();
        GrappleHook hook = USurviveRegistries.GRAPPLE_HOOK.get(getHookType());
        if(hook==null || maxHooks < 1) return ItemUseResult.empty();
        if(maxHooks < 2 && !ctx.getAction().isRightClick()) return ItemUseResult.empty();

        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();

        GrapplerHolder grappler = PlayerMemory.getOrCreate(p)
            .getPlayerDataHolderOrCompute(GrapplerHolder.class, GrapplerHolder::new);

        int id;
        if(maxHooks > 1) id = ctx.getAction().isRightClick() ? 0 : 1;
        else id = 0;

        ActiveGrappleHook previous = grappler.getGrapple(id);
        if(previous != null){
            previous.forceRemove(p);
            return ItemUseResult.cancelled();
        }
        if(p.hasCooldown(item.item())){
            return ItemUseResult.cancelled();
        }
        if(cooldown > 0) p.setCooldown(item.item(), cooldown);
        var threw = grappler.throwNewHook(hook, buildGrappleContext(ctx, id));
        if(threw.hasSwapHandAction()){
            Lang.GRAPPLE_USE_SWAP_HANDS.use(p);
        }
        Lang.GRAPPLE_USE.playAt(p.getLocation());
        if(p.getGameMode() != GameMode.CREATIVE){
            if(itemCountUse > 0){
                item.amount(item.amount()-1);
            }else Crux.handlers().item().damageItem(item.item(), 1, p);
        }
        return ItemUseResult.cancelled();
    }

    @Override
    public boolean isInteractable(@NotNull ItemUseContext ctx) {
        if(ctx.getAction() == Action.PHYSICAL) return false;
        CruxItem item = ctx.getItem();
        if(item.item().getData(DataComponentTypes.CONSUMABLE) != null) return false;
        return true;
    }
}
