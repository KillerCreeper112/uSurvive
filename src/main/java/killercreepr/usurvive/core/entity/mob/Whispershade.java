package killercreepr.usurvive.core.entity.mob;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.usurvive.core.entity.mob.goals.WhispershadeGoal;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Whispershade extends UCruxMob {
    public Whispershade(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Bee.class, e ->{
            e.customName(Component.text("Whispershade"));
            e.setCustomNameVisible(false);
            e.setSilent(true);
            load(e);
            e.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(
                e.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue() * .7
            );
            e.getAttribute(Attribute.FLYING_SPEED).setBaseValue(
                e.getAttribute(Attribute.FLYING_SPEED).getBaseValue() * 1.2
            );

            e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(
                20D
            );
            e.setHealth(e.getAttribute(Attribute.MAX_HEALTH).getBaseValue());

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

        if(!CruxAttribute.hasAttributeData(mob, CruxAttribute.ATTACK_RANGE)){
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(
                mob.getWidth() + 1.35D
            ));
        }
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        Crux.getServer().getMobGoals().getAllGoals(mob).forEach(goal ->{
            String id = goal.getKey().getNamespacedKey().asString();
            if(id.equalsIgnoreCase("minecraft:bee_wander")) return;
            if(id.contains("bee_") || id.contains("hive") || id.contains("parent") || id.contains("tempt") ||
                id.contains("breed") || id.contains("flower")){
                Crux.getServer().getMobGoals().removeGoal(mob, goal.getKey());
                return;
            }
        });

        CompletableFuture<ActiveModel> model = new DesignEntity(mob).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        applyWhenCompleteModel(mob, model);
        CruxGoalUtil.addIfNotPresent(mob, WhispershadeGoal.class, 0, () -> new WhispershadeGoal(mob).model(model));
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{
            MobCategory.MONSTER, MobCategory.ENEMY, USurviveMobCategory.ELDRITCH
        };
    }
}
