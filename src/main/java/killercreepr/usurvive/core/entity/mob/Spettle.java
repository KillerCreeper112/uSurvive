package killercreepr.usurvive.core.entity.mob;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.usurvive.core.entity.mob.goals.SpettleGoal;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Spettle extends UCruxMob {
    public Spettle(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            e.customName(Component.text("Spettle"));
            e.setCustomNameVisible(false);
            e.setSilent(true);
            load(e);
            e.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(
                e.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue() * 1.1
            );

            e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(
                32D
            );
            e.setHealth(e.getAttribute(Attribute.MAX_HEALTH).getBaseValue());

            double width = e.getWidth();
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(5D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(11D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-10));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(0.15));
            CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.baseModifier(e.getAttribute(Attribute.MOVEMENT_SPEED).getValue()));
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void onModelApplied(Mob mob) {
        super.onModelApplied(mob);
        if(CruxAttribute.hasAttributeData(mob)) return;
        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(
            mob.getWidth() + .8D
        ));
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CompletableFuture<ActiveModel> model = new DesignEntity(mob).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        applyWhenCompleteModel(mob, model);
        CruxGoalUtil.addIfNotPresent(mob, SpettleGoal.class, 0, () -> new SpettleGoal(mob).model(model));
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{
            MobCategory.ENEMY, MobCategory.MONSTER
        };
    }
}
