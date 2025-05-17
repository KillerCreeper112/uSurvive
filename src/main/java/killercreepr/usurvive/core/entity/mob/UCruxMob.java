package killercreepr.usurvive.core.entity.mob;

import killercreepr.cruxentities.entity.SimpleCruxMob;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class UCruxMob extends SimpleCruxMob {

    public UCruxMob(@NotNull Key key) {
        super(key);
    }

    public void applyWhenCompleteModel(Mob mob, CompletableFuture<?> future){
        future.whenComplete((model, throwable) ->{
            onModelApplied(mob);
        });
    }

    public void onModelApplied(Mob mob){
    }
}
