package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ToxicSurvivorHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("advancement/toxic_survivor");

    protected final Consumer<Entity> onComplete;

    public ToxicSurvivorHolder(@NotNull Key key, @NotNull EntityMemory parent, Consumer<Entity> onComplete) {
        super(key, parent);
        this.onComplete = onComplete;
    }
    public ToxicSurvivorHolder(@NotNull EntityMemory parent, Consumer<Entity> onComplete) {
        this(KEY, parent, onComplete);
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
        if(!(e instanceof LivingEntity living)) return true;
        return !living.hasPotionEffect(PotionEffectType.POISON);
    }

    protected int duration = 0;
    @Override
    public void tick(@NotNull Entity entity) {
        duration++;
        if(duration >= 3600){
            markForRemoval();
            onComplete.accept(entity);
        }
    }
}
