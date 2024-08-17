package killercreepr.usurvive.data.entity;

import killercreepr.crux.Crux;
import killercreepr.crux.data.entity.PlayerMemory;
import killercreepr.crux.data.entity.PlayerTickedDataHolder;
import killercreepr.usurvive.mechanics.grapple.GrappleHookHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrapplerHolder extends PlayerTickedDataHolder {
    public static final Key KEY = Crux.key("grappler");
    public GrapplerHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
    }
    public GrapplerHolder( @NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    protected final List<GrappleHookHolder> grapples = new ArrayList<>();

    public List<GrappleHookHolder> getGrapples() {
        return grapples;
    }

    public void addGrapple(@NotNull GrappleHookHolder grapple){
        grapples.add(grapple);
    }

    public GrappleHookHolder getGrapple(int index){
        return index >= grapples.size() ? null : grapples.get(index);
    }

    public void remove(){
        for(GrappleHookHolder h : grapples){
            h.remove();
        }
        grapples.clear();
    }

    @Override
    protected void onTick(@NotNull Player e) {

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
