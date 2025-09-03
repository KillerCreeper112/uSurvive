package killercreepr.usurvive.core.mechanics.blast_tempest;

import killercreepr.crux.core.Crux;
import killercreepr.usurvive.core.mechanics.BlastTempestComponent;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class GenericBlastTempestPlayer {
    protected final List<BlastTempestComponent.RayData> rays;
    protected final Consumer<GenericBlastTempestPlayer> onDone;
    protected final Supplier<Boolean> isValid;
    protected final int consumeTime;

    protected BukkitRunnable runnable;
    protected int tick = 0;
    protected int index = 0;
    protected int nextTarget = 0; // tick target for the next sound

    public GenericBlastTempestPlayer(List<BlastTempestComponent.RayData> rays, Consumer<GenericBlastTempestPlayer> onDone, Supplier<Boolean> isValid, int consumeTime) {
        this.rays = rays;
        this.onDone = onDone;
        this.isValid = isValid;
        this.consumeTime = consumeTime;
    }
    public abstract boolean onTick(BlastTempestComponent.RayData ray);

    public BlastTempestComponent.RayData getCurrentRay() {
        return rays.get(index);
    }

    public boolean hasNext() {
        return index + 1 < rays.size();
    }

    public BlastTempestComponent.RayData next(double scale) {
        index++;
        BlastTempestComponent.RayData sound = getCurrentRay();
        nextTarget += (int) Math.round(1 * scale);
        return sound;
    }

    public void stop() {
        try {
            runnable.cancel();
        } catch (NullPointerException | IllegalStateException ignored) {}
    }

    public void onDone(){

    }

    public void onDoneInvalid(){

    }

    public int getDoneInTicks(){
        return consumeTime - tick;
    }

    public void onTick(){

    }

    protected boolean gotTarget = false;
    public GenericBlastTempestPlayer play() {
        int totalWait = rays.size();
        if (totalWait == 0) totalWait = 1;

        // scaling factor to match consume time
        double scale = (double) (consumeTime-2) / totalWait;

        // initialize target with first sound
        BlastTempestComponent.RayData current = getCurrentRay();
        nextTarget = (int) Math.floor(1 * scale);

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                tick++;

                if(!gotTarget){
                    gotTarget = onTick(getCurrentRay());
                }

                if (tick >= nextTarget) {
                    if (!hasNext()) {
                        cancel();
                        onDone();
                        if(onDone != null) onDone.accept(GenericBlastTempestPlayer.this);
                        return;
                    }
                    next(scale);
                    gotTarget = false;
                }

                if (!isValid.get()) {
                    cancel();
                    onDoneInvalid();
                    return;
                }
                onTick();
            }
        };
        runnable.runTaskTimer(Crux.getMainPlugin(), 0L, 1L);
        return this;
    }
}
