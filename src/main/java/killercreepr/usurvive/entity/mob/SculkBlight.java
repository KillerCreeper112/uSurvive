package killercreepr.usurvive.entity.mob;

import killercreepr.cruxattributes.attribute.CruxAttribute;
import killercreepr.cruxattributes.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.GenericCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import killercreepr.usurvive.entity.mob.goals.SculkBlightGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;

public class SculkBlight extends GenericCruxMob {
    public SculkBlight(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(
                e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 1.5
            );
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_RANGE, new CruxAttributeModifier(3D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, new CruxAttributeModifier(5D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_AOE, new CruxAttributeModifier(.25D));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_SPEED, new CruxAttributeModifier(6D));
            new ModelEntity(e, key.value());
            load(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob m)) return;
        new DesignEntity(m).getModel(key.value()).ifPresent(model ->{
            Bukkit.getMobGoals().addGoal(m, 0, new SculkBlightGoal(m, model));
        });
    }
}
