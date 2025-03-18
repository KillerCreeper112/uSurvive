package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.util.CruxEntityUtil;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EchoOfTheAbyssHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("advancement/echo_of_the_abyss");

    protected final Consumer<Entity> onComplete;

    public EchoOfTheAbyssHolder(@NotNull Key key, @NotNull EntityMemory parent, Consumer<Entity> onComplete) {
        super(key, parent);
        this.onComplete = onComplete;
    }
    public EchoOfTheAbyssHolder(@NotNull EntityMemory parent, Consumer<Entity> onComplete) {
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
        if(duration % 20 != 0) return false;
        return !isInDarkness(e);
    }

    public static boolean isInDarkness(Entity e){
        if(e instanceof Player d){
            switch (d.getGameMode()){
                case CREATIVE, SPECTATOR -> { return false; }
            }
        }
        if(e instanceof LivingEntity d){
            return isDark(d.getEyeLocation().getBlock());
        }
        return isDark(e.getLocation().getBlock());
    }

    public static boolean isDark(Block b){
        return b.getLightLevel() < 1;
    }

    protected int duration = 0;
    @Override
    public void tick(@NotNull Entity entity) {
        duration++;
        if(duration >= 24_000){
            markForRemoval();
            if(!isInDarkness(entity)) return;
            Crux.scheduler().runTask(() ->{
                if(!CruxEntityUtil.isValid(entity)) return;
                onComplete.accept(entity);
            });
        }
    }
}
