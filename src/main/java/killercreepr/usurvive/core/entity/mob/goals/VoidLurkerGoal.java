package killercreepr.usurvive.core.entity.mob.goals;

import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttackHandler;
import killercreepr.usurvive.core.entity.mob.goals.data.StrongMobAttack;
import killercreepr.usurvive.core.util.UKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VoidLurkerGoal extends CruxMobModeledGoal implements Listener {
    protected final MobAttackHandler attackHandler;
    public VoidLurkerGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_WITHER_AMBIENT, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,1.7f);
            }

            @Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_WITHER_SHOOT, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,1.7f);
            }

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_WITHER_HURT, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,1.7f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_WITHER_DEATH, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f,1.7f);
            }
        });

        attackHandler = new MobAttackHandler(mob, this, List.of(
            new StrongMobAttack(1) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.6D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .9D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -.15D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.KNOCKBACK_RESISTANCE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 2, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public void onTick() {
                    super.onTick();
                    int time = getPlayingAnimationTimeTicks(getAnimationID());
                    if(time >= 14 && time <= 20){
                        var result = rawHitLogic(true, null, (org.bukkit.entity.Entity[]) null);
                        if(result != null){
                            result.getHit().forEach(hit ->{
                                if(attackHandler.wasHitWithin(hit, 5)) return;
                                attackHandler.hit(hit);
                                attack(hit);
                                if(hit instanceof LivingEntity lv){
                                    lv.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                                }
                            });
                        }
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
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.6D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .25D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .5D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.KNOCKBACK_RESISTANCE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 2, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public int getHitTime() {
                    return 12;
                }
            },
            new StrongMobAttack(3) {
                @Override
                public void onUse() {

                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.6D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .25D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.5D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.KNOCKBACK_RESISTANCE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 2, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public int getHitTime() {
                    return 12;
                }
            }
        ), List.of());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        playAnimation("hurt", false);
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
                CruxAttribute.removeModifier(mob, CruxAttribute.MOVEMENT_SPEED, targetSpeed);
                stopAnimation("target_pose");
            }
        }else{
            if(!hadTargetLastTick){

                CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                    CruxAttributeModifier.modifier(targetSpeed, 0.1, CruxAttribute.Operation.MULTIPLY));
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
