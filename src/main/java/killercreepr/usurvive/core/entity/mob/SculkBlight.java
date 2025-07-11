package killercreepr.usurvive.core.entity.mob;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import killercreepr.usurvive.core.entity.mob.goals.SculkBlightGoal;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SculkBlight extends UCruxMob {
    public SculkBlight(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            e.customName(Component.text("Sculk Blight"));
            e.setCustomNameVisible(false);
            e.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(
                e.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue() * 1.5
            );
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(4D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(.25D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-6D));
            new ModelEntity(e, key.value());
            e.setSilent(true);
            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public Set<MobCategory> getCategories() {
        return Set.of(
            MobCategory.MONSTER, MobCategory.SCULK, MobCategory.ENEMY
        );
    }

    @Override
    public void onModelApplied(Mob mob) {
        super.onModelApplied(mob);

        if(!CruxAttribute.hasAttributeData(mob, CruxAttribute.ATTACK_RANGE)){
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(
                mob.getWidth() + .85D
            ));
        }
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob m)) return;
        CompletableFuture<ActiveModel> model = new DesignEntity(m).getOrAddModelAsync(key.value());
        applyWhenCompleteModel(m, model);
        CruxGoalUtil.addIfNotPresent(m, SculkBlightGoal.class, 0, () -> new SculkBlightGoal(m).model(model));
    }
}
