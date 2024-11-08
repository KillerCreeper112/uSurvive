package killercreepr.usurvive.entity.mob;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.util.CruxGoalUtil;
import killercreepr.cruxattributes.attribute.CruxAttribute;
import killercreepr.cruxattributes.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.usurvive.entity.mob.goals.DownmanGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Downman extends SimpleCruxMob {
    public Downman(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Husk.class, e ->{
            load(e);
            e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(
                e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 1.2
            );
            double width = e.getWidth();
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_RANGE, new CruxAttributeModifier(
                2D + width
            ));

            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_AOE, new CruxAttributeModifier(
                .3D
            ));

            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_SPEED, new CruxAttributeModifier(
                -10
            ));

            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK, new CruxAttributeModifier(
                16D
            ));

            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, new CruxAttributeModifier(
                8D
            ));

            CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED, new CruxAttributeModifier(
                e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()
            ));
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CompletableFuture<ActiveModel> model = new DesignEntity(mob).getOrAddModelAsync(key.value());
        CruxGoalUtil.addIfNotPresent(mob, DownmanGoal.class, 0, () -> new DownmanGoal(mob).model(model));
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{
            MobCategory.MONSTER
        };
    }
}
