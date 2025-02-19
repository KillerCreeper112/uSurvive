package killercreepr.usurvive.core.entity.mob;

import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SleeperDisplayEntity extends SimpleCruxMob {
    public SleeperDisplayEntity(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, ArmorStand.class, e ->{
            e.setAI(false);
            e.setSilent(true);
            e.setCanTick(false);
            e.setCanMove(false);
            e.setPersistent(false);
            e.setRemoveWhenFarAway(true);
            e.setMarker(true);
            e.setInvisible(true);
            //e.setVisibleByDefault(false);
            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{
            MobCategory.OBJECT
        };
    }
}
