package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.ParticleBuilder;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.component.CruxComponents;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxedBoundingBox;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttackHandler;
import killercreepr.usurvive.core.entity.mob.goals.data.StrongMobAttack;
import killercreepr.usurvive.core.util.UKey;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuzzrotGoal extends CruxMobModeledGoal implements Listener {
    protected final MobAttackHandler attackHandler;
    public BuzzrotGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_BEE_LOOP, 0.4f,1.8f);
            }

            @Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_BEE_STING, 0.4f,1.5f);
            }
            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_BEE_HURT, 0.4f,.6f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_BEE_DEATH, 0.4f,.6f);
            }
        });

        attackHandler = new MobAttackHandler(mob, this, List.of(
            new StrongMobAttack(1) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.6D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 1D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                }

                public void atck(BoundingBox box){
                    mob.getWorld().getNearbyEntities(box, e -> e instanceof LivingEntity le && isValidNaturalTarget(le)).forEach(hit ->{
                        if(attackHandler.wasHitWithin(hit, 6)) return;
                        attackHandler.hit(hit);
                        attack(hit);
                    });
                }

                @Override
                public void onTick() {
                    super.onTick();

                    int time = getPlayingAnimationTimeTicks(getAnimationID());
                    if(time >= 9 && time <= 14){
                        atck(
                            CruxedBoundingBox.boundingBox(getLeftHandPos(), 1.5)
                        );
                        atck(
                            CruxedBoundingBox.boundingBox(getRightHandPos(), 1.5)
                        );
                    }
                }

                @Override
                public int getHitTime() {
                    return 0;
                }
            },
            new StrongMobAttack(2) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.8D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 1.5D, CruxAttribute.Operation.MULTIPLY));
                }

                public void atck(BoundingBox box){
                    mob.getWorld().getNearbyEntities(box, e -> e instanceof LivingEntity le && isValidNaturalTarget(le)).forEach(hit ->{
                        if(attackHandler.wasHitWithin(hit, 6)) return;
                        attackHandler.hit(hit);
                        attack(hit);
                    });
                }

                @Override
                public void onTick() {
                    super.onTick();

                    int time = getPlayingAnimationTimeTicks(getAnimationID());
                    if(time >= 11 && time <= 18){
                        atck(
                            CruxedBoundingBox.boundingBox(getLeftHandPos(), 1.5)
                        );
                        atck(
                            CruxedBoundingBox.boundingBox(getRightHandPos(), 1.5)
                        );
                    }
                }

                @Override
                public int getHitTime() {
                    return 0;
                }
            },
            new StrongMobAttack(3) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -5D, CruxAttribute.Operation.MULTIPLY));
                    CreateSound.sound(Sound.ENTITY_BEE_HURT, 2f).playAt(mob);
                }

                @Override
                public boolean canUseAttack() {
                    if(CruxMath.hasOccurredWithin(lastUsed, 2400)){
                       return false;
                    }
                    double percent = mob.getHealth() / mob.getAttribute(Attribute.MAX_HEALTH).getValue();
                    if(percent > 0.6f) return false;
                    return super.canUseAttack();
                }

                long lastUsed;
                @Override
                public void onTick() {
                    super.onTick();

                    int time = getPlayingAnimationTimeTicks(getAnimationID());
                    if(time >= 21 && !CruxMath.hasOccurredWithin(lastUsed, 60)){
                        lastUsed = System.currentTimeMillis();
                        spawnBees();
                    }
                }

                public void spawnBees(){
                    new ParticleBuilder(Particle.ELECTRIC_SPARK)
                        .location(mob.getEyeLocation())
                        .offset(.5, .5, .5)
                        .count(CruxMath.random(8, 12))
                        .spawn()
                    ;
                    int amount = CruxMath.random(4, 7);
                    while(amount > 0){
                        amount--;
                        USurviveMobs.BUZZROT_BEE.spawn(mob.getEyeLocation(), e ->{
                            CruxEntity.entity(e).set(CruxComponents.OWNER, mob.getUniqueId());
                        });
                    }
                    CreateSound.sound(Sound.ENTITY_EVOKER_CAST_SPELL, 2f).playAt(mob);
                }

                @Override
                public int getHitTime() {
                    return 0;
                }
            }
        ), List.of());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        playAnimation("hurt", false);
    }

    public Location getLeftHandPos(){
        return getModel().getBone("left_hand_pos").get().getLocation();
    }

    public Location getRightHandPos(){
        return getModel().getBone("right_hand_pos").get().getLocation();
    }


    @Override
    public boolean preAttemptAttack() {
        if(attackHandler.preAttemptAttack()){
            return super.preAttemptAttack();
        }
        return false;
    }

    @Override
    public void attacked(@NotNull CruxEntityDamageEvent event) {
        super.attacked(event);
        if(attackHandler.isUsingStrongAttack()) return;
        String id = generateAttackAnimationID();
        if(id == null) return;
        playAnimation(id, true);
    }


    public String generateAttackAnimationID(){
        return "attack_" + CruxMath.random(1,3);
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return !CruxMob.isInCategory(target, MobCategory.ENEMY) && super.isValidNaturalTarget(target);
    }

    protected static final NamespacedKey targetSpeed = UKey.TARGET_SPEED;
    protected boolean hadTargetLastTick = false;
    public void movementTick(){
        boolean hasTarget = target != null;

        if(target == null){
            if(hadTargetLastTick){
                mob.getAttribute(Attribute.MOVEMENT_SPEED).removeModifier(targetSpeed);
                stopAnimation("target_pose");
            }
        }else{
            if(!hadTargetLastTick){
                mob.getAttribute(Attribute.MOVEMENT_SPEED).addTransientModifier(
                    new AttributeModifier(targetSpeed, .1, AttributeModifier.Operation.MULTIPLY_SCALAR_1)
                );
                playAnimation("target_pose", true);
            }
        }
        if(hasTarget == hadTargetLastTick) return;
        hadTargetLastTick = hasTarget;
    }


    protected final AnimationHandler.DefaultProperty WALK_VICIOUS_RUN = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "vicious_run", .2D, .2D, 1D
    );
    /*protected final AnimationHandler.DefaultProperty WALK_SWIM = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "swim", .2D, .2D, 1D
    );*/
    protected final AnimationHandler.DefaultProperty WALK_NORMAL = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "walk", .2D, .2D, 1D
    );

    public AnimationHandler.DefaultProperty getDefaultWalk(){
        //if(swimmer.isSwimming()) return WALK_SWIM;
        if(target == null) return WALK_NORMAL;
        return WALK_VICIOUS_RUN;
    }

    public void animationPropertyTick(){
        ActiveModel model = getModel();
        if(model ==null) return;
        AnimationHandler.DefaultProperty current = model.getAnimationHandler().getDefaultProperty(ModelState.WALK);
        AnimationHandler.DefaultProperty should = getDefaultWalk();
        if(current.equals(should)) return;
        stopAnimation(current.getAnimation());
        model.getAnimationHandler().setDefaultProperty(should);
        playAnimation(should.getAnimation(), false);
    }


    @Override
    public void tick() {
        //animationPropertyTick();
        super.tick();
        attackHandler.tick();
        movementTick();
    }
}
