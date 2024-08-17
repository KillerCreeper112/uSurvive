package killercreepr.usurvive.mechanics.grapple;

import killercreepr.crux.Crux;
import killercreepr.crux.data.Holder;
import killercreepr.crux.data.entity.EntityMemory;
import killercreepr.crux.data.entity.EntityTickedDataHolder;
import killercreepr.crux.util.CruxEntity;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PufferFish;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GrappleHookHolder extends EntityTickedDataHolder {
    public static GrappleHookHolder createNew(@NotNull Entity owner, @NotNull Location start, @NotNull Vector vel){
        LivingEntity hook = start.getWorld().spawn(start, PufferFish.class, e ->{
            e.setInvulnerable(true);
            e.setInvisible(true);
            e.setVelocity(vel);
            e.setAware(false);
            e.setLeashHolder(owner);
            e.setPuffState(3);
        });

        EntityMemory mem = EntityMemory.getOrCreate(hook);
        GrappleHookHolder holder = new GrappleHookHolder(mem);
        holder.setHolder(owner);
        mem.getDataHolders().register(holder);
        return holder;
    }

    public static final Key KEY = Crux.key("grapple_hook");
    public GrappleHookHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }

    public GrappleHookHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    protected Holder<Entity> holder;

    public Holder<Entity> getHolder() {
        return holder;
    }

    public void setHolder(Holder<Entity> holder) {
        this.holder = holder;
    }

    public void setHolder(@NotNull Entity holder) {
        this.setHolder(Holder.weakReference(holder));
    }

    public boolean hasHolder(){
        return holder != null && holder.value() != null;
    }

    public void remove(){
        Entity entity = getParent().value();
        if(entity==null) return;
        entity.remove();
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Entity e) {
        if(e==null) return true;
        Entity holder = this.holder == null ? null : this.holder.value();
        if(holder==null) return true;
        return !holder.getWorld().equals(e.getWorld());
    }

    @Override
    public void tick(@NotNull Entity entity) {
        Entity holder = this.holder.value();
        if(holder==null) return;
        if(!(entity instanceof LivingEntity mob)) return;

        if(entity.wouldCollideUsing(entity.getBoundingBox().expand(.001))){
            Vector vel = entity.getLocation().toVector().subtract(CruxEntity.getEyeOrLocation(holder).toVector())
                .multiply(.04f);
            entity.setVelocity(entity.getVelocity().add(vel));
            mob.setAI(false);
            return;
        }
        //todo particle
        mob.setAI(true);
    }
}
