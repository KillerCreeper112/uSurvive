package killercreepr.usurvive.core.entity.mob.goals;

import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class BuzzrotBeeGoal extends CruxMobGoal implements Listener {
    //protected final MobAttackHandler attackHandler;
    public BuzzrotBeeGoal(@NotNull Mob mob) {
        super(mob);
        /*attackHandler = new MobAttackHandler(mob, this, List.of(

        ), List.of());*/
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return target instanceof HumanEntity e && CruxEntityUtil.isValid(target) && e.getGameMode() != GameMode.CREATIVE && e.getGameMode() != GameMode.SPECTATOR;
    }
}
