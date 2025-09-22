package killercreepr.usurvive.core.entity.mob;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.usurvive.core.entity.mob.goals.NPCControlGoal;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class NPCControl extends UCruxMob {
    public NPCControl(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            e.customName(Component.text("NPC Control"));
            e.setCustomNameVisible(false);
            load(e);

            /*e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(
                80D
            );
            e.setHealth(e.getAttribute(Attribute.MAX_HEALTH).getBaseValue());*/

            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(2D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(11D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-11));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(0.1));
            CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.baseModifier(e.getAttribute(Attribute.MOVEMENT_SPEED).getValue()));
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void onModelApplied(Mob mob) {
        super.onModelApplied(mob);
        if(!CruxAttribute.hasAttributeData(mob, CruxAttribute.ATTACK_RANGE)){
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(
                mob.getWidth() + 1.1D
            ));
        }
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        Crux.getServer().getMobGoals().removeAllGoals(mob);
        //CompletableFuture<ActiveModel> model = new DesignEntity(mob).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        //applyWhenCompleteModel(mob, model);
        CruxGoalUtil.addIfNotPresent(mob, NPCControlGoal.class, 0, () -> new NPCControlGoal(mob));
    }

    /*@Override
    public Set<MobCategory> getCategories() {
        return Set.of(
            MobCategory.ENEMY, MobCategory.MONSTER, USurviveMobCategory.ELDRITCH
        );
    }*/
}
