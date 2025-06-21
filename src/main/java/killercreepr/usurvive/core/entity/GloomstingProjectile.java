package killercreepr.usurvive.core.entity;

import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.usurvive.core.entity.mob.UCruxMob;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class GloomstingProjectile extends UCruxMob {
    public GloomstingProjectile(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Snowball.class, e ->{
            e.customName(Component.text("Gloomsting Spit"));
            e.setCustomNameVisible(false);

            e.setItem(new ItemStack(Material.SLIME_BALL));

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
