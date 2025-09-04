package killercreepr.usurvive.core.entity.mob;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.predicate.ItemPredicate;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxTeamComparer;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.entity.mob.goals.UseRangedItemGoal;
import killercreepr.usurvive.core.mechanics.blast_tempest.BlastTempestPlayer;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TNTTempestMob extends UCruxMob {
    protected final Class<? extends Mob> type;
    protected final int attackIntervalMin;
    protected final float attackRadius;
    protected final BiConsumer<UseRangedItemGoal, Boolean> changePos;
    public TNTTempestMob(@NotNull Key key, Class<? extends Mob> type, int attackIntervalMin, float attackRadius, BiConsumer<UseRangedItemGoal, Boolean> changePos) {
        super(key);
        this.type = type;
        this.attackIntervalMin = attackIntervalMin;
        this.attackRadius = attackRadius;
        this.changePos = changePos;
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, type, e ->{
            //e.setCustomNameVisible(false);
            //e.setSilent(true);
            load(e);

            e.getEquipment().setItem(EquipmentSlot.HAND, Crux.handlers().item().getItem(Crux.key("tnt_tempest")).value());

            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CruxGoalUtil.addIfNotPresent(mob, UseRangedItemGoal.class, 0, () ->
            new UseRangedItemGoal(
                mob, ItemPredicate.fromType(Crux.key("tnt_tempest")),
                1D, attackIntervalMin, attackRadius, -1f,
                goal ->{
                }
            ){
                @Override
                public void changePose(boolean on) {
                    if(changePos != null){
                        changePos.accept(this, on);
                        return;
                    }
                    super.changePose(on);
                }

                @Override
                public void startUsingItem() {
                    ItemStack item = mob.getEquipment().getItem(getUsableItemSlot());
                    if(CruxItem.isEmpty(item)) return;
                    CruxItem crux = CruxItem.wrap(item);
                    var tempestData = crux.getOrDefaultData(USurviveComponents.TNT_TEMPEST);
                    if(tempestData == null) return;
                    super.startUsingItem();

                    int consumeTime;
                    var data =  crux.item().getData(DataComponentTypes.CONSUMABLE);
                    if(data == null) consumeTime = 100;
                    else{
                        consumeTime = (int) (data.consumeSeconds() * 20f);
                    }

                    CruxTeamComparer teamComparer = CruxTeamComparer.teamComparer(false, team -> !team.allowFriendlyFire());
                    new BlastTempestPlayer(
                        tempestData.getRays(),
                        null,
                        null,
                        () -> CruxEntityUtil.isValid(mob) && isUsingItem() && item.equals(mob.getEquipment().getItem(getUsableItemSlot())),
                        consumeTime,
                        () -> mob.getEyeLocation(),
                        e ->{
                            if(e.equals(mob)) return false;
                            if(!CruxMobGoal.UNDESIRED_BEHAVIOR.test(e)) return false;
                            if(teamComparer.areOnSameTeam(mob, e)) return false;
                            return true;
                        },
                        tempestData.getRange(),
                        tempestData.getYawDegrees(),
                        tempestData.getPitchDegrees(),
                        tempestData.getLaunchSound(),
                        tempestData.getLockOnSound()
                    ).play();
                }
            }
        );
    }
}
