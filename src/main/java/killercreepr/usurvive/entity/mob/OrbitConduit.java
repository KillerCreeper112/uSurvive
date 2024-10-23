package killercreepr.usurvive.entity.mob;

import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class OrbitConduit extends SimpleCruxMob {
    public OrbitConduit(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, ArmorStand.class, e ->{
            ModelEntity modelEntity = new ModelEntity(e, key.value());
            modelEntity.playAnimation("wind", true);
            modelEntity.playAnimation("rotate", true);
            e.setGravity(false);
            e.setMarker(true);
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
