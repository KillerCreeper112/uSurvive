package killercreepr.usurvive.core.entity;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.usurvive.core.entity.mob.UCruxMob;
import killercreepr.usurvive.core.entity.mob.goals.BuzzrotGoal;
import killercreepr.usurvive.core.entity.mob.goals.SnareEnchantGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SnareEnchant extends UCruxMob {
    public SnareEnchant(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            e.setPersistent(false);
            e.setRemoveWhenFarAway(true);
            e.setSilent(true);
            e.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(-99D);
            e.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(-99D);
            e.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(9999D);

            e.setCollidable(false);

            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void onModelApplied(Mob mob) {
        super.onModelApplied(mob);
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;

        CompletableFuture<ActiveModel> model = new DesignEntity(mob).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        applyWhenCompleteModel(mob, model);
        CruxGoalUtil.addIfNotPresent(mob, SnareEnchantGoal.class, 0, () -> new SnareEnchantGoal(mob).model(model));
    }

    @Override
    public Set<MobCategory> getCategories() {
        return Set.of(
            MobCategory.OBJECT
        );
    }
}
