package killercreepr.usurvive.core.entity.mob;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.usurvive.core.entity.mob.goals.VaneGoal;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Vane extends UCruxMob {
    public Vane(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Allay.class, e ->{
            e.customName(Component.text("Vane"));
            e.setCustomNameVisible(false);
            e.setSilent(true);
            load(e);
            e.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(
                0D
            );
            e.getAttribute(Attribute.FLYING_SPEED).setBaseValue(
                0.0001D
            );
            e.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(
                64D
            );

            e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(70D);
            e.setHealth(70D);

            double width = e.getWidth();
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(width + 7D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(5D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(20D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-100));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(0.3));
            CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.baseModifier(e.getAttribute(Attribute.MOVEMENT_SPEED).getValue()));
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CompletableFuture<ActiveModel> model = new DesignEntity(mob).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        model.whenComplete((m, throwable) ->{
            m.getAnimationHandler().playAnimation("hover", 0, 0, 1, true);
        });
        applyWhenCompleteModel(mob, model);
        CruxGoalUtil.addIfNotPresent(mob, VaneGoal.class, 0, () -> new VaneGoal(mob).model(model));
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{
            MobCategory.MONSTER, MobCategory.ENEMY, USurviveMobCategory.ELDRITCH
        };
    }
}
