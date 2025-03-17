package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.entity.CruxMob;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LivingInfernoHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("advancement/living_inferno");
    public LivingInfernoHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }
    public LivingInfernoHolder( @NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    protected boolean remove = false;
    public void markForRemoval(){
        remove = true;
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Entity e) {
        if(remove) return true;
        if(super.shouldRemoveFromMemory(e)) return true;
        if(e == null) return true;
        if(e.getFireTicks() < 1) return true;
        return !CruxMath.hasOccurredWithin(lastKill, 1200);
    }

    @Override
    public void tick(@NotNull Entity entity) {

    }

    protected int streak = 0;
    protected long lastKill;

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public long getLastKill() {
        return lastKill;
    }

    public void setLastKill(long lastKill) {
        this.lastKill = lastKill;
    }
}
