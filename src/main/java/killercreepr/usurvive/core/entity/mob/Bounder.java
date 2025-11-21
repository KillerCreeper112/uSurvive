package killercreepr.usurvive.core.entity.mob;

import com.destroystokyo.paper.entity.ai.Goal;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.entity.mob.goals.BounderGoal;
import killercreepr.usurvive.core.entity.mob.goals.SkeletonWolfGoal;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Wolf;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Bounder extends UCruxMob {
    public Bounder(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            e.customName(Component.text("Bounder"));
            e.setCustomNameVisible(false);
            e.setSilent(true);
            load(e);

            e.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(
                32
            );
            e.getAttribute(Attribute.MAX_HEALTH).setBaseValue(
                30D
            );
            e.setHealth(e.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
            e.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(
                e.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue() * 1.15
            );

            //CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(7D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(10D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-11));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(0.1));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(1.9));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(1.5D));
            CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.baseModifier(e.getAttribute(Attribute.MOVEMENT_SPEED).getValue()));
            if(consumer != null) consumer.accept(e);
        });
    }

    public String generateBounderSkinID(){
        return "bounder_" + CruxMath.random(0, USurvivePlugin.inst().values().BOUNDER_SKIN_AMOUNT().value().intValue());
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        for (Goal<Mob> goal : Crux.getServer().getMobGoals().getAllGoals(mob)) {
            if(goal.getKey().getNamespacedKey().asString().equalsIgnoreCase("minecraft:nearest_attackable")){
                Crux.getServer().getMobGoals().removeGoal(mob, goal);
            }
        }

        CruxGoalUtil.addIfNotPresent(mob, BounderGoal.class, 0, () -> new BounderGoal(mob));

        String skin = CruxTag.get(e, "bounder_skin", PersistentDataType.STRING, generateBounderSkinID());
        var disguise = new PlayerDisguise(skin);
        disguise.setName("");
        disguise.setNameVisible(false);
        DisguiseAPI.disguiseEntity(e, disguise);
    }

    @Override
    public Set<MobCategory> getCategories() {
        return Set.of(
            MobCategory.ENEMY
        );
    }
}
