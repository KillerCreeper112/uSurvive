package killercreepr.usurvive.data.entity;

import killercreepr.crux.Crux;
import killercreepr.crux.data.entity.EntityMemory;
import killercreepr.crux.data.entity.PlayerMemory;
import killercreepr.crux.data.entity.PlayerTickedDataHolder;
import killercreepr.crux.util.CruxEntity;
import killercreepr.usurvive.entity.USurviveMobs;
import killercreepr.usurvive.mechanics.grapple.GrappleHookHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.PufferFish;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GrapplerHolder extends PlayerTickedDataHolder {
    public static final Key KEY = Crux.key("grappler");
    public GrapplerHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
    }
    public GrapplerHolder( @NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    public GrappleHookHolder throwNewHook(@NotNull Location at, @NotNull Entity owner, int id, @Nullable Consumer<LivingEntity> consumer){
        return createNewHook(at, owner, id, e ->{
            e.setVelocity(CruxEntity.getEyeOrLocation(owner).getDirection());
            if(consumer != null) consumer.accept(e);
        });
    }

    public GrappleHookHolder createNewHook(@NotNull Location at, @NotNull Entity owner, int id, @Nullable Consumer<LivingEntity> consumer){
        LivingEntity hook = USurviveMobs.GRAPPLE_HOOK.spawnAt(PufferFish.class, at, e ->{
            e.setLeashHolder(owner);
            e.setPuffState(3);
            if(consumer != null) consumer.accept(e);
        });

        EntityMemory mem = EntityMemory.getOrCreate(hook, d ->{
            GrappleHookHolder holder = new GrappleHookHolder(d, id);
            holder.setHolder(owner);
            d.getDataHolders().register(holder);
        });
        return mem.getDataHolderOrThrow(GrappleHookHolder.class);
    }

    protected int maxGrapples;
    public int getMaxGrapples() {
        return maxGrapples;
    }

    public void setMaxGrapples(int maxGrapples) {
        this.maxGrapples = maxGrapples;
    }

    protected final Map<Integer, GrappleHookHolder> grapples = new HashMap<>();

    public Map<Integer, GrappleHookHolder> getGrapples() {
        return grapples;
    }

    public GrappleHookHolder removeGrapple(int id){
        return grapples.remove(id);
    }

    public void putGrapple(@NotNull GrappleHookHolder grapple){
        GrappleHookHolder previous = grapples.put(grapple.getId(), grapple);
        if(previous != null) previous.remove();
    }

    public GrappleHookHolder getGrapple(int id){
        return grapples.get(id);
    }

    public void reset(){
        maxGrapples = 0;
        remove();
    }

    public void remove(){
        for(GrappleHookHolder h : grapples.values()){
            h.remove();
        }
        grapples.clear();
    }

    @Override
    protected void onTick(@NotNull Player e) {
        grapples.values().removeIf(d -> !d.isValid());
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Player e) {
        return grapples.isEmpty() || maxGrapples < 1;
    }

    @Override
    protected void removingFromMemory(@Nullable Player e) {
        super.removingFromMemory(e);
        remove();
    }
}
