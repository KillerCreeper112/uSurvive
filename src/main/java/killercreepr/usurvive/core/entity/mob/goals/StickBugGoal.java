package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.MaterialTags;
import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StickBugGoal extends CruxMobModeledGoal {
    public static final Key STRONG_ATTACK_KEY = Crux.key("strong_attack");
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("stick_bug"));

    public StickBugGoal(@NotNull Mob mob) {
        this(KEY, mob);
    }

    public StickBugGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @Nullable CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_CREAKING_DEATH, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,2f);
            }

            @Override
            public @Nullable CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_CREAKING_STEP, net.kyori.adventure.sound.Sound.Source.HOSTILE,.3f, 1.8f);
            }

            @Override
            public @Nullable CreateSound step() {
                return CreateSound.sound(Sound.ENTITY_CREAKING_STEP, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,2f);
            }

            @Override
            public @Nullable CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_CREAKING_ATTACK, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,2f);
            }

            @Override
            public @Nullable CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_CREAKING_AMBIENT, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,2f);
            }
        });
    }

    protected int strongAttackCooldown;
    public boolean canUseStrongAttack(){
        return strongAttackCooldown < 1 && !isUsingStrongAttack();
    }

    public boolean isUsingStrongAttack(){
        return maxAttackTime > 0;
    }

    public void combatTick(){
        if(!isUsingStrongAttack()){
            combatNotUsingStrongAttackTick();
            return;
        }
        combatUsingStrongAttackTick();
    }

    public void combatNotUsingStrongAttackTick(){
        if(strongAttackCooldown > 0){
            strongAttackCooldown--;
        }
    }

    public void onHitAtTime(){
        this.attemptAttack();
    }

    public void onCombatStrongAttackComplete(){
        maxAttackTime = 0;
        hitAt = 0;
        currentAttackID = 0;
        CruxAttribute.removeModifiers(mob, STRONG_ATTACK_KEY);
    }

    public void combatUsingStrongAttackTick(){
        attackTime++;
        if(hitAt == attackTime){
            onHitAtTime();
            hitAt = 0;
        }
        if(attackTime >= maxAttackTime){
            onCombatStrongAttackComplete();
            return;
        }
        onCombatUsingStrongAttackTick();
    }
    public void onCombatUsingStrongAttackTick(){}

    public int generateStrongAttackID(){
        return 1;//CruxMath.random(1,2);
    }

    public int getHitAtTime(int attackID){
        return switch(attackID){
            default -> 5;
        };
    }

    public void onUseStrongAttack(int attackID){
        switch(attackID){
            case 1 ->{
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .15D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
            }
        }
    }

    public void onUseStrongAttackApplyAllAttributes(int attackID){
        CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
            CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -5D, CruxAttribute.Operation.MULTIPLY));
    }

    public int useStrongAttack(){
        strongAttackCooldown = CruxMath.random(30, 80);
        int atck = generateStrongAttackID();
        String id = "attack_strong_" + atck;
        playAnimation(id, true);
        this.maxAttackTime = (int) Math.ceil(getAnimationLengthTicks(id) / 2f);
        this.attackTime = 0;

        this.hitAt = getHitAtTime(atck);
        onUseStrongAttackApplyAllAttributes(atck);
        onUseStrongAttack(atck);
        return atck;
    }

    protected int attackTime = 0;
    protected int maxAttackTime = 0;
    protected int hitAt = 0;
    protected int currentAttackID;
    @Override
    public boolean preAttemptAttack() {
        if(canUseStrongAttack()){
            currentAttackID = useStrongAttack();
            return false;
        }
        if(isUsingStrongAttack() && hitAt != attackTime) return false;
        return super.preAttemptAttack();
    }

    @Override
    public void attacked(@NotNull CruxEntityDamageEvent event) {
        super.attacked(event);
        if(isUsingStrongAttack()) return;
        String id = generateAttackAnimationID();
        if(id == null) return;
        playAnimation(id, true);
    }


    public String generateAttackAnimationID(){
        return "attack_" + CruxMath.random(1,1);
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return !CruxMob.isInCategory(target, MobCategory.ENEMY) && super.isValidNaturalTarget(target);
    }

    @Override
    public double getFindTargetRange() {
        AttributeInstance i = this.mob.getAttribute(Attribute.FOLLOW_RANGE);
        return i == null ? (double)10.0F : i.getValue();
    }

    public void movementTick(){
        double moveSpeed = CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED);
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(moveSpeed);
    }

    @Override
    @EventHandler
    public void entityDamageMob(EntityDamageByEntityEvent event) {
        super.entityDamageMob(event);
        if(!event.getEntity().equals(mob)) return;
        if(!(event.getDamager() instanceof LivingEntity dmger)) return;
        var equip = dmger.getEquipment();
        if(equip == null) return;
        ItemStack item = equip.getItemInMainHand();
        if(CruxItem.isEmpty(item)) return;
        if(MaterialTags.AXES.isTagged(item)){
            event.setDamage(event.getDamage()*1.25D);
        }
    }

    protected int tick = 0;
    @Override
    public void tick() {
        if(targetBlock != null){
            if(!isValidTargetBlock(targetBlock)){
                setTargetBlock(null);
                return;
            }
            Location center = targetBlock.getLocation().toCenterLocation();
            double distance = center.distance(mob.getLocation());
            if(distance > 10){
                setTargetBlock(null);
                return;
            }
            if(distance < 2.5){
                mob.getPathfinder().stopPathfinding();
                mob.lookAt(center);
            }else{
                Location moveTo = CruxLoc.shiftToward(center, mob.getLocation(), 2D);
                moveTo(moveTo);
            }
            return;
        }

        tick++;
        if(tick % 6 == 0){
            Block nearby = getCampfireNearby();
            if(nearby != null){
                setTargetBlock(nearby);
                return;
            }
        }

        super.tick();
        combatTick();
        movementTick();
    }

    public Block getTargetBlock() {
        return targetBlock;
    }

    public void setTargetBlock(Block targetBlock) {
        this.targetBlock = targetBlock;
    }

    public boolean isValidTargetBlock(Block b){
        return b.getWorld().equals(mob.getWorld()) && isValidCampfire(b);
    }

    protected Block targetBlock;
    public Block getCampfireNearby(){
        for(Block b : CruxLoc.getNearbyBlocks(mob.getLocation().getBlock(), 8)){
            if(isValidCampfire(b)) return b;
        }
        return null;
    }

    public boolean isValidCampfire(Block b){
        return b.getBlockData() instanceof Campfire f && f.isLit();
    }
}
