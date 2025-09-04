package killercreepr.usurvive.core.entity;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.usurvive.core.entity.memory.SmallTNTMemory;
import killercreepr.usurvive.core.entity.mob.UCruxMob;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SmallTNT extends UCruxMob {
    public SmallTNT(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            e.customName(Component.text("Small TNT"));
            e.setCustomNameVisible(false);
            e.setSilent(true);

            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK,
                CruxAttributeModifier.baseModifier(12));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.baseModifier(6));

            CruxTag.set(e, "fuse_ticks", PersistentDataType.INTEGER, 60);

            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(e instanceof Mob mob){

            Bukkit.getMobGoals().getAllGoals(mob).forEach(goal ->{
                Bukkit.getMobGoals().removeGoal(mob, goal);
            });

            CompletableFuture<ActiveModel> model = new DesignEntity(mob).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
            applyWhenCompleteModel(mob, model);
        }

        var mem = new SmallTNTMemory(e);
        mem.setFuseTicks(CruxTag.get(e, "fuse_ticks", PersistentDataType.INTEGER, 60));
        EntityMemory.register(mem, true);
    }

    @Override
    public Set<MobCategory> getCategories() {
        return Set.of(
            MobCategory.OBJECT
        );
    }
}
