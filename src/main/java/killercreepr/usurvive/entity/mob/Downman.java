package killercreepr.usurvive.entity.mob;

import killercreepr.cruxattributes.attribute.CruxAttribute;
import killercreepr.cruxattributes.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.GenericCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import killercreepr.usurvive.entity.mob.goals.DownmanGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class Downman extends GenericCruxMob {
    public Downman(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location) {
        return location.getWorld().spawn(location, Husk.class, e ->{
            new ModelEntity(e, key.value());
            load(e);
            e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(
                e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 1.2
            );
            double width = e.getWidth();
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_RANGE, new CruxAttributeModifier(
                2D + width
            ));

            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_AOE, new CruxAttributeModifier(
                .3D
            ));

            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_SPEED, new CruxAttributeModifier(
                -10
            ));

            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK, new CruxAttributeModifier(
                16D
            ));

            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, new CruxAttributeModifier(
                8D
            ));

            CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED, new CruxAttributeModifier(
                e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()
            ));
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob m)) return;
        new DesignEntity(m).getModel(key.value()).ifPresent(model ->{
            Bukkit.getMobGoals().addGoal(m, 0, new DownmanGoal(m, model));
        });
    }
}
