package killercreepr.usurvive.entity.mob;

import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxGoalUtil;
import killercreepr.cruxattributes.attribute.CruxAttribute;
import killercreepr.cruxattributes.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import killercreepr.usurvive.entity.mob.goals.EchoGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Skeleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class Echo extends SimpleCruxMob {
    public static final Key KEY = Crux.key("echo");
    public Echo(@NotNull Key key) {
        super(key);
    }
    public Echo(){
        this(KEY);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Skeleton.class, e ->{
            e.setShouldBurnInDay(false);
            e.getEquipment().clear();
            e.setPersistent(true);
            e.setRemoveWhenFarAway(false);

            CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED, new CruxAttributeModifier(
                e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()
            ));
            e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0D);

            new ModelEntity(e, "grave").setLockPitch(true).setLockYaw(true);
            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        new DesignEntity(mob).getModel("grave").ifPresent(model ->{
            CruxGoalUtil.addIfNotPresent(mob, CruxMobGoal.defaultKey(), 0, () -> new EchoGoal(mob, model));
        });
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{
            MobCategory.ETERNAL
        };
    }
}
