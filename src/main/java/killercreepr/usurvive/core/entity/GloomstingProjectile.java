package killercreepr.usurvive.core.entity;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.usurvive.core.entity.memory.ParticleTrailHolder;
import killercreepr.usurvive.core.entity.mob.UCruxMob;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
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
    public void load(@NotNull Entity e) {
        super.load(e);
        EntityMemory.getOrCreateDataHolder(e, ParticleTrailHolder.class, mem -> new ParticleTrailHolder(
            mem, Color.fromRGB(0x74F43D)
        ));
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{
            MobCategory.OBJECT
        };
    }
}
