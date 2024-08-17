package killercreepr.usurvive.entity;

import killercreepr.cruxentities.entity.GenericCruxMob;
import killercreepr.cruxentities.persistence.CruxEntitiesPersistTags;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.PufferFish;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class GrappleHookMob extends GenericCruxMob {
    public GrappleHookMob(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location at) {
        return at.getWorld().spawn(at, PufferFish.class, e ->{
            defaultConsumer().accept(e);
            e.setPuffState(3);
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
            CruxEntitiesPersistTags.ENTITY.set(e, this.key);
        });
    }
}
