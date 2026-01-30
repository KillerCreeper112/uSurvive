package killercreepr.usurvive.core.entity.mob.guide;

import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.CruxComponents;
import killercreepr.crux.core.util.CruxAttributeUtil;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.usurvive.core.entity.mob.goals.guide.GuidePathGoal;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class GenericNPCHusk extends SimpleCruxMob {
    public GenericNPCHusk(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location loc, @Nullable Consumer<Entity> consumer) {
        return loc.getWorld().spawn(loc, Husk.class, e ->{
            e.setSilent(true);
            var crux = CruxEntity.entity(e);
            crux.set(CruxComponents.INVULNERABLE, true);
            e.setAdult();

            CruxAttributeUtil.editInstance(e, Attribute.MOVEMENT_SPEED, instance ->{
                instance.setBaseValue(instance.getBaseValue()*1.3D);
            });

            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        var disguise = new PlayerDisguise("guide_1");
        disguise.setNameVisible(false);
        disguise.setName("");
        DisguiseAPI.disguiseEntity(mob, disguise);
        Crux.getServer().getMobGoals().removeAllGoals(mob);
        CruxGoalUtil.addIfNotPresent(mob, GuidePathGoal.class, 0, () -> new GuidePathGoal(mob));
    }
}
