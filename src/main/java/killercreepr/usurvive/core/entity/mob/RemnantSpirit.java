package killercreepr.usurvive.core.entity.mob;

import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import killercreepr.crux.core.persistence.CruxPersist;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import killercreepr.cruxentities.persistence.CruxEntitiesPersist;
import killercreepr.usurvive.core.entity.mob.goals.RemnantSpiritGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

public class RemnantSpirit extends SimpleCruxMob {
    public RemnantSpirit(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        throw new UnsupportedOperationException("Requires a player input.");
    }

    public void copyStats(Entity mob, Player p, int level){
        addBase(mob, p, CruxAttribute.ATTACK_PIERCE, 0D);
        addBase(mob, p, CruxAttribute.ATTACK_SPEED, 11D * level);
        addBase(mob, p, CruxAttribute.ATTACK_AOE, .2D * level);
        addBase(mob, p, CruxAttribute.ATTACK_KNOCKBACK, 10D * level);
        addBase(mob, p, CruxAttribute.ATTACK_RANGE, 1.5D * level);

        CruxAttribute.addModifier(
            mob, CruxAttribute.ATTACK_DAMAGE,
            CruxAttributeModifier.baseModifier(2.5D + (EntityDamager.getDamage(p) * level))
        );
    }

    private void addBase(Entity mob, Player p, CruxAttribute attribute, double fallback){
        CruxAttribute.addModifier(
            mob, attribute,
            CruxAttributeModifier.baseModifier(CruxAttribute.get(p, attribute, fallback))
        );
    }

    public @NotNull Entity spawn(int level, @NotNull Player p, @NotNull Location location, @Nullable Consumer<Entity> consumer){
        return location.getWorld().spawn(location, Pig.class, e ->{
            CruxPersist.OWNER.set(e, p.getUniqueId());
            e.setPersistent(false);
            e.setRemoveWhenFarAway(false);
            e.setSilent(true);

            copyStats(e, p, level);

            ModelEntity model = new ModelEntity(e, key.value());
            model.playAnimation("float", true);
            model.getModel().getBones().values().forEach(bone ->{
                handleBone(bone, p);
            });

            load(e);
            if(consumer != null) consumer.accept(e);
            CruxEntitiesPersist.ENTITY.set(e, this.key);
        });
    }

    public void handleBone(@NotNull ModelBone bone, @NotNull Player p){
        bone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(limb ->{
            limb.setTexture(p);
        });
        bone.getChildren().values().forEach(child ->{
            handleBone(child, p);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CruxGoalUtil.addIfNotPresent(mob, CruxMobGoal.defaultKey(), 0, () -> new RemnantSpiritGoal(mob));
    }

    @Override
    public Set<MobCategory> getCategories() {
        return Set.of(
            MobCategory.UNDEAD, MobCategory.ENEMY, MobCategory.MONSTER
        );
    }
}
