package killercreepr.usurvive.core.data.entity;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.usurvive.api.mechanics.whip.ActiveWhip;
import killercreepr.usurvive.api.mechanics.whip.WhipHook;
import killercreepr.usurvive.api.mechanics.whip.context.WhipContext;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WhipperHolder extends PlayerTickedDataHolder {
    public static final Key KEY = Crux.key("whipper");
    public WhipperHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
    }
    public WhipperHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    public ActiveWhip throwNewHook(@NotNull WhipHook hook, @NotNull WhipContext ctx){
        ActiveWhip active = createNewHook(hook, ctx);
        putWhip(active);
        return active;
    }

    public ActiveWhip createNewHook(@NotNull WhipHook hook, @NotNull WhipContext ctx){
        return hook.throwNew(ctx);
    }

    protected final Map<Key, ActiveWhip> whips = new ConcurrentHashMap<>();

    public Map<Key, ActiveWhip> getWhips() {
        return whips;
    }

    public ActiveWhip removeWhip(Key id){
        return whips.remove(id);
    }

    public void putWhip(@NotNull ActiveWhip grapple){
        ActiveWhip previous = whips.put(grapple.key(), grapple);
        if(previous != null) previous.remove(getParent().value());
    }

    public ActiveWhip getWhip(Key id){
        return whips.get(id);
    }

    public void reset(Entity owner){
        remove(owner);
    }

    public void remove(Entity owner){
        for(ActiveWhip h : whips.values()){
            h.remove(owner);
        }
        whips.clear();
    }

    @Override
    protected void onTick(@NotNull Player e) {
        if(!e.isValid()){
            reset(e);
            return;
        }
        whips.values().removeIf(d ->{
            if(!d.isValid(e)){
                d.remove(e);
                return true;
            }
            d.tick(e);
            return false;
        });
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Player e) {
        return whips.isEmpty();
    }

    @Override
    protected void removingFromMemory(@Nullable Player e) {
        super.removingFromMemory(e);
        remove(e);
    }

    @Override
    public void scheduledRemoval(Entity e) {
        super.scheduledRemoval(e);
        if(e == null) return;
        remove(e);
    }
}
