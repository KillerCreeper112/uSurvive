package killercreepr.usurvive.entity.mob;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.util.CruxGoalUtil;
import killercreepr.cruxattributes.attribute.CruxAttribute;
import killercreepr.cruxattributes.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import killercreepr.usurvive.entity.mob.goals.SculkBlightGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SculkBlight extends SimpleCruxMob {
    public SculkBlight(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(
                e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 1.5
            );
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_RANGE, new CruxAttributeModifier(2.5D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, new CruxAttributeModifier(4D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_AOE, new CruxAttributeModifier(.25D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_SPEED, new CruxAttributeModifier(-6D));
            new ModelEntity(e, key.value());
            e.setSilent(true);
            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{
            MobCategory.MONSTER, MobCategory.SCULK
        };
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob m)) return;
        CompletableFuture<ActiveModel> model = new DesignEntity(m).getOrAddModelAsync(key.value());
        CruxGoalUtil.addIfNotPresent(m, SculkBlightGoal.class, 0, () -> new SculkBlightGoal(m).model(model));
    }
}
