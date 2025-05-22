package killercreepr.usurvive.core.entity.mob;

import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.usurvive.core.entity.mob.goals.BuzzrotBeeGoal;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BuzzrotBee extends UCruxMob {
    public BuzzrotBee(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Bee.class, e ->{
            e.customName(Component.text("Buzzrot Bee"));
            e.setCustomNameVisible(false);
            load(e);
            e.getAttribute(Attribute.FLYING_SPEED).setBaseValue(
                e.getAttribute(Attribute.FLYING_SPEED).getBaseValue() * 1.2
            );

            e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(
                e.getAttribute(Attribute.MAX_HEALTH).getBaseValue() * 1.5D
            );
            e.setHealth(e.getAttribute(Attribute.MAX_HEALTH).getBaseValue());

            double width = e.getWidth();
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(width + 1.2D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(5D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(11D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-10));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(0.15));
            //CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.baseModifier(e.getAttribute(Attribute.MOVEMENT_SPEED).getValue()));
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        //CompletableFuture<ActiveModel> model = new DesignEntity(mob).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        CruxGoalUtil.addIfNotPresent(mob, BuzzrotBeeGoal.class, 0, () -> new BuzzrotBeeGoal(mob));
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{
            MobCategory.ENEMY, MobCategory.MONSTER, USurviveMobCategory.ELDRITCH
        };
    }
}
