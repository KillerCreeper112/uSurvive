package killercreepr.usurvive.core.entity.mob;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.predicate.ItemPredicate;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.cruxattributes.core.component.CruxAttributeComponents;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.entity.mob.goals.UseRangedItemGoal;
import killercreepr.usurvive.core.mechanics.BoneFluteComponent;
import killercreepr.usurvive.core.mechanics.ConfettiCannonComponent;
import killercreepr.usurvive.core.mechanics.bone_flute.FlutePlayer;
import killercreepr.usurvive.core.mechanics.bone_flute.FluteSoundSequence;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BoneFluteMob extends UCruxMob {
    protected final Class<? extends Mob> type;
    protected final int attackIntervalMin;
    protected final float attackRadius;
    protected final BiConsumer<UseRangedItemGoal, Boolean> changePos;
    public BoneFluteMob(@NotNull Key key, Class<? extends Mob> type, int attackIntervalMin, float attackRadius, BiConsumer<UseRangedItemGoal, Boolean> changePos) {
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

            e.getEquipment().setItem(EquipmentSlot.HAND, Crux.handlers().item().getItem(Crux.key("bone_flute")).value());

            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        CruxGoalUtil.addIfNotPresent(mob, UseRangedItemGoal.class, 0, () ->
            new UseRangedItemGoal(
                mob, ItemPredicate.fromType(Crux.key("bone_flute")),
                1D, attackIntervalMin, attackRadius,
                goal ->{
                    ItemStack item = mob.getEquipment().getItem(goal.getUsableItemSlot());
                    CruxItem crux = CruxItem.wrap(item);
                    var data = crux.getOrDefaultData(USurviveComponents.BONE_FLUTE);
                    Location loc = mob.getEyeLocation();
                    loc = CruxLoc.lookAt(loc, mob.getTarget().getLocation().add(0, mob.getTarget().getHeight()*.5, 0));
                    data.launch(mob, loc);
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
                    var boneFlute = crux.getOrDefaultData(USurviveComponents.BONE_FLUTE);
                    if(boneFlute == null) return;

                    super.startUsingItem();

                    int consumeTime;
                    var data =  crux.item().getData(DataComponentTypes.CONSUMABLE);
                    if(data == null) consumeTime = 100;
                    else{
                        consumeTime = (int) (data.consumeSeconds() * 20f);
                    }

                    FluteSoundSequence sequence = boneFlute.sequenceOrDefault();

                    new FlutePlayer(
                        sequence,
                        BoneFluteComponent.genericPlaySound(mob),
                        null,
                        () -> isUsingItem() && item.equals(mob.getEquipment().getItem(getUsableItemSlot()))
                    ).play(consumeTime);
                }
            }
        );
    }
}
