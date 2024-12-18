package killercreepr.usurvive.core.mechanics.grapple;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import killercreepr.usurvive.api.mechanics.Grapple;
import killercreepr.usurvive.api.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.api.mechanics.grapple.GrappleHook;
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
    public SimpleGrapple(@NotNull Key hook, int maxHooks, int cooldown) {
        this.hook = hook;
        this.maxHooks = maxHooks;
        this.cooldown = cooldown;
    }

    public int getMaxHooks() {
        return maxHooks;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @NotNull
    public Key getHookType() {
        return hook;
    }

    @Override
    public @NotNull ItemUseResult onUse(@NotNull ItemUseContext ctx) {
        GrappleHook hook = USurviveRegistries.GRAPPLE_HOOK.get(getHookType());
        if(hook==null || maxHooks < 1) return ItemUseResult.empty();
        Player p = ctx.getPlayer();
        CruxItem item = ctx.getItem();

        GrapplerHolder grappler = PlayerMemory.getOrCreate(p)
            .getPlayerDataHolderOrCompute(GrapplerHolder.class, GrapplerHolder::new);

        int id;
        if(maxHooks > 1) id = ctx.getAction().isRightClick() ? 0 : 1;
        else id = 0;

        ActiveGrappleHook previous = grappler.getGrapple(id);
        if(previous != null){
            previous.remove(p);
            return ItemUseResult.cancelled();
        }
        if(p.hasCooldown(item.item().getType())){
            return ItemUseResult.cancelled();
        }
        if(cooldown > 0) p.setCooldown(item.item().getType(), cooldown);
        grappler.throwNewHook(hook, new GrappleContext(
            p.getEyeLocation(), p, id, 1D
        ));
        Lang.GRAPPLE_USE.playAt(p.getLocation());
        if(p.getGameMode() != GameMode.CREATIVE){
            Crux.handlers().item().damageItem(item.item(), 1, p);
        }
        return ItemUseResult.cancelled();
    }

    @Override
    public boolean isUsable(@NotNull ItemUseContext ctx) {
        if(ctx.getAction() == Action.PHYSICAL) return false;
        return true;
    }
}
