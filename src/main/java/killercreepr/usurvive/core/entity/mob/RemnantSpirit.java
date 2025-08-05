package killercreepr.usurvive.core.entity.mob;

import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxclaims.core.entity.mob.goal.AstralProjectionGoal;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
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

    public @NotNull Entity spawn(@NotNull Player p, @NotNull Location location, @Nullable Consumer<Entity> consumer){
        return location.getWorld().spawn(location, Pig.class, e ->{
            e.setPersistent(false);
            e.setRemoveWhenFarAway(false);
            e.setSilent(true);

            ModelEntity model = new ModelEntity(e, key.value());
            model.playAnimation("float", true);
            model.getModel().getBones().values().forEach(bone ->{
                handleBone(bone, p);
            });

            load(e);
            if(consumer != null) consumer.accept(e);
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

        //CruxGoalUtil.addIfNotPresent(mob, AstralProjectionGoal.defaultKey(), 0, () -> new AstralProjectionGoal(mob));
    }

    @Override
    public Set<MobCategory> getCategories() {
        return Set.of(
            MobCategory.UNDEAD, MobCategory.ENEMY, MobCategory.MONSTER
        );
    }
}
