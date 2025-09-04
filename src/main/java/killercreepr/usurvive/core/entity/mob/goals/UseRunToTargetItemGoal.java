package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.ai.GoalKey;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.item.predicate.ItemPredicate;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pose;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class UseRunToTargetItemGoal extends CruxMobModeledGoal implements Listener {
    public UseRunToTargetItemGoal(@NotNull Mob mob, ItemPredicate usableItem, double speedModifier, int attackIntervalMin, float attackRadius, float useItemRadius, Consumer<UseRunToTargetItemGoal> onUseItem) {
        super(mob);
        this.usableItem = usableItem;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.useItemDistanceSqr = useItemRadius * useItemRadius;
        this.onUseItem = onUseItem;
    }

    public UseRunToTargetItemGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob, ItemPredicate usableItem, double speedModifier, int attackIntervalMin, float attackRadius, float useItemRadius, Consumer<UseRunToTargetItemGoal> onUseItem) {
        super(key, mob);
        this.usableItem = usableItem;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.useItemDistanceSqr = useItemRadius * useItemRadius;
        this.onUseItem = onUseItem;
    }

    protected final ItemPredicate usableItem;
    protected final double speedModifier;
    protected int attackIntervalMin;
    protected final float attackRadiusSqr;
    protected final float useItemDistanceSqr;
    protected int attackTime = -1;
    protected int seeTime;
    protected int strafingTime = -1;

    protected int usingItemTicks;
    protected boolean usingItem = false;
    protected final Consumer<UseRunToTargetItemGoal> onUseItem;

    public int getMaxUseTime(){
        ItemStack item = getUsableItem();
        if(CruxItem.isEmpty(item)) return 20;
        Consumable consumable = item.getData(DataComponentTypes.CONSUMABLE);
        if(consumable == null) return 20;
        return (int) (consumable.consumeSeconds() * 20f);
    }

    public ItemStack getUsableItem(){
        EquipmentSlot slot = getUsableItemSlot();
        if(slot == null) return null;
        return mob.getEquipment().getItem(slot);
    }

    public boolean isUsingItem(){
        return usingItem;//!CruxItem.isEmpty(mob.getActiveItem());
    }

    public boolean canUse() {
        return this.mob.getTarget() != null && this.isHoldingItem();
    }

    public EquipmentSlot getUsableItemSlot(){
        for (EquipmentSlot slot : AttributeBukkitAdaptor.HAND_SLOTS) {
            if(!mob.canUseEquipmentSlot(slot)) continue;
            ItemStack item = mob.getEquipment().getItem(slot);
            if(CruxItem.isEmpty(item)) continue;
            if(usableItem.test(item)) return slot;
        }
        return null;
    }

    protected boolean isHoldingItem() {
        return getUsableItemSlot() != null;
    }

    public void performUseItem(int ticksUsingItem){
        onUseItem.accept(this);
    }

    public void startUsingItem(){
        usingItem = true;
        changePose(true);
        //mob.startUsingItem(getUsableItemSlot());
    }

    public void changePose(boolean on){
        mob.setPose(Pose.SHOOTING, on);
    }

    public void stopUsingItem(){
        usingItem = false;
        usingItemTicks = 0;
        changePose(false);
    }
    public int getTicksUsingItem(){
        return usingItemTicks;//mob.getActiveItemUsedTime();
    }

    public int getWarnAtTick(){
        return (int) (getMaxUseTime() * .8f);
    }

    public void warn(){

    }

    @Override
    public void stop() {
        super.stop();
        this.seeTime = 0;
        this.attackTime = -1;
    }

    public boolean isCloseEnoughToUse(double distanceSqr){
        return distanceSqr <= useItemDistanceSqr;
    }

    @Override
    public void tick() {
        //super.tick();
        LivingEntity target = mob.getTarget();
        if(target == null) return;

        if(!canUse()) return;

        double d = this.mob.getLocation().distanceSquared(target.getLocation());
        boolean hasLineOfSight = mob.hasLineOfSight(target);
        boolean flag = this.seeTime > 0;
        if (hasLineOfSight != flag) {
            this.seeTime = 0;
        }

        if (hasLineOfSight) {
            ++this.seeTime;
        } else {
            --this.seeTime;
        }

        if (!(d > (double)this.attackRadiusSqr) && this.seeTime >= 20) {
            this.mob.getPathfinder().stopPathfinding();
        } else {
            moveTo(target, this.speedModifier);
        }

        if (isUsingItem()) {
            usingItemTicks++;
            if (!hasLineOfSight && this.seeTime < -60) {
                stopUsingItem();
            } else if (hasLineOfSight) {
                int ticksUsingItem = getTicksUsingItem();
                if (ticksUsingItem >= getMaxUseTime()) {
                    performUseItem(ticksUsingItem);
                    stopUsingItem();
                    this.attackTime = this.attackIntervalMin;
                }else{
                    if(ticksUsingItem == getWarnAtTick()){
                        warn();
                    }
                }
            }
        } else if (--this.attackTime <= 0 && this.seeTime >= -60 && isCloseEnoughToUse(d)) {
            startUsingItem();
        }
    }
}
