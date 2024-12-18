package killercreepr.usurvive.core.entity;

import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.persistence.CruxEntitiesPersist;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.PufferFish;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class GrappleHookMob extends SimpleCruxMob {
    public GrappleHookMob(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location at, @Nullable Consumer<Entity> consumer) {
        return at.getWorld().spawn(at, PufferFish.class, e ->{
            defaultConsumer().accept(e);
            e.setPuffState(3);
            if(consumer != null) consumer.accept(e);
        });
    }

    public Consumer<Mob> defaultConsumer(){
        return e ->{
            e.setInvulnerable(true);
            e.setAware(false);
        };
    }

    public <T extends Entity> @NotNull T spawnAt(@NotNull Class<T> type, @NotNull Location at, @Nullable Consumer<T> consumer){
        return at.getWorld().spawn(at, type, e ->{
            if(e instanceof Mob mob) defaultConsumer().accept(mob);
            if(consumer != null) consumer.accept(e);
            CruxEntitiesPersist.ENTITY.set(e, this.key);
        });
    }
}
