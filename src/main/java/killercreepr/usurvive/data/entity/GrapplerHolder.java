package killercreepr.usurvive.data.entity;

import killercreepr.crux.Crux;
import killercreepr.crux.data.entity.PlayerMemory;
import killercreepr.crux.data.entity.PlayerTickedDataHolder;
import killercreepr.usurvive.mechanics.grapple.ActiveGrappleHook;
import killercreepr.usurvive.mechanics.grapple.GrappleContext;
import killercreepr.usurvive.mechanics.grapple.GrappleHook;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GrapplerHolder extends PlayerTickedDataHolder {
    public static final Key KEY = Crux.key("grappler");
    public GrapplerHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
    }
    public GrapplerHolder( @NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    public ActiveGrappleHook throwNewHook(@NotNull GrappleHook hook, @NotNull GrappleContext ctx){
        ActiveGrappleHook active = createNewHook(hook, ctx);
        putGrapple(active);
        return active;
    }

    public ActiveGrappleHook createNewHook(@NotNull GrappleHook hook, @NotNull GrappleContext ctx){
        return hook.throwNew(ctx);
    }

    protected final Map<Integer, ActiveGrappleHook> grapples = new HashMap<>();

    public Map<Integer, ActiveGrappleHook> getGrapples() {
        return grapples;
    }

    public ActiveGrappleHook removeGrapple(int id){
        return grapples.remove(id);
    }

    public void putGrapple(@NotNull ActiveGrappleHook grapple){
        ActiveGrappleHook previous = grapples.put(grapple.getId(), grapple);
        if(previous != null) previous.remove();
    }

    public ActiveGrappleHook getGrapple(int id){
        return grapples.get(id);
    }

    public void reset(){
        remove();
    }

    public void remove(){
        for(ActiveGrappleHook h : grapples.values()){
            h.remove();
        }
        grapples.clear();
    }

    @Override
    protected void onTick(@NotNull Player e) {
        grapples.values().removeIf(d ->{
            if(!d.isValid(e)) return true;
            d.tick(e);
            return false;
        });
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Player e) {
        return grapples.isEmpty();
    }

    @Override
    protected void removingFromMemory(@Nullable Player e) {
        super.removingFromMemory(e);
        remove();
    }
}
