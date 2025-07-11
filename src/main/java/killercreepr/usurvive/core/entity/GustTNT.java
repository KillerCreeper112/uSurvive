package killercreepr.usurvive.core.entity;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.usurvive.core.entity.memory.GustTNTHolder;
import killercreepr.usurvive.core.entity.mob.UCruxMob;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

public class GustTNT extends UCruxMob {
    public GustTNT(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, TNTPrimed.class, e ->{
            e.customName(Component.text("Gust TNT"));
            e.setCustomNameVisible(false);
            e.setSilent(true);

            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK,
                CruxAttributeModifier.baseModifier(30));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK_UP,
                CruxAttributeModifier.baseModifier(15));

            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        EntityMemory.getOrCreateDataHolder(e, GustTNTHolder.class, GustTNTHolder::new);
    }

    @Override
    public Set<MobCategory> getCategories() {
        return Set.of(
            MobCategory.OBJECT
        );
    }
}
