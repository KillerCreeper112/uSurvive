package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.ParticleBuilder;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.usurvive.core.entity.memory.SpettleProjectileHolder;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttack;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttackHandler;
import killercreepr.usurvive.core.entity.mob.goals.data.StrongMobAttack;
import killercreepr.usurvive.core.util.UKey;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class SpettleGoal extends CruxMobModeledGoal implements Listener {
    protected final MobAttackHandler attackHandler;
    public SpettleGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_SLIME_JUMP, 0.4f,.5f);
            }

            @Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_SLIME_SQUISH, 0.4f,1.5f);
            }

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_MAGMA_CUBE_HURT, 0.4f,.5f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_SLIME_DEATH, 0.4f,.6f);
            }
        });

        attackHandler = new MobAttackHandler(mob, this, List.of(
            new StrongMobAttack(1) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.6D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .8D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .3D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public int getHitTime() {
                    return 14;
                }
            },
            new StrongMobAttack(2) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.4D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .3D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK_UP,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 12D, CruxAttribute.Operation.ADD));
                }

                long lastSound;
                @Override
                public void onFinish() {
                    super.onFinish();
                }

                @Override
                public void onTick() {
                    super.onTick();
                    int time = getPlayingAnimationTimeTicks(getAnimationID());
                    if(time >= 5 && time <= 20){
                        BoundingBox box = mob.getBoundingBox().expand(2.5, 1, 2.5);
                        mob.getWorld().getNearbyEntities(box, e -> e instanceof LivingEntity l && isValidNaturalTarget(l)).forEach(hit ->{
                            if(attackHandler.wasHitWithin(hit, 8)) return;
                            attackHandler.hit(hit);
                            attack(hit);
                        });
                        if(!CruxMath.hasOccurredWithin(lastSound, 5)){
                            CreateSound.sound(Sound.ENTITY_SLIME_SQUISH, .5f).playAt(mob);
                            lastSound = System.currentTimeMillis();
                        }
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
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.1D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 1D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .3D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 3D, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public void onTick() {
                    super.onTick();
                    int time = getPlayingAnimationTimeTicks(getAnimationID());
                    if(time >= 20){
                        BoundingBox box = mob.getBoundingBox().expand(2, 2, 2);
                        mob.getWorld().getNearbyEntities(box, e -> e instanceof LivingEntity l && isValidNaturalTarget(l)).forEach(hit ->{
                            if(attackHandler.wasHitWithin(hit, 8)) return;
                            attackHandler.hit(hit);
                            attack(hit);
                        });
                        new ParticleBuilder(Particle.SNEEZE)
                            .location(getLeftHandPos())
                            .spawn()
                            .location(getRightHandPos())
                            .spawn()
                        ;
                    }
                }

                @Override
                public int getHitTime() {
                    return 0;
                }
            },
            new StrongMobAttack(4) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -5D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 1D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .3D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 3D, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public void onFinish() {
                    super.onFinish();

                    Location loc = mob.getLocation().add(0, mob.getHeight()/2, 0);
                    new ParticleBuilder(Particle.EXPLOSION)
                        .location(loc)
                        .offset(.5, .5, .5)
                        .spawn()

                        .particle(Particle.FLASH)
                        .spawn()
                    ;
                    CreateSound.sound(Sound.ENTITY_GENERIC_EXPLODE, 1.5f).playAt(mob);
                    CreateSound.sound(Sound.ENTITY_SLIME_SQUISH, 1.5f).playAt(mob);
                    mob.getWorld().spawn(mob.getLocation(), AreaEffectCloud.class, e ->{
                        e.addCustomEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0), true);
                        e.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0), true);
                        e.addCustomEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 0), true);
                        e.setColor(Color.PURPLE);
                        e.setRadius(4f);
                        e.setDuration(200);
                    });
                }

                @Override
                public void onTick() {
                    super.onTick();
                    int time = getPlayingAnimationTimeTicks(getAnimationID());
                    if(time >= 13 && time < 50){
                        BoundingBox box = mob.getBoundingBox().expand(2.5, 1, 2.5);
                        mob.getWorld().getNearbyEntities(box, e -> e instanceof LivingEntity l && isValidNaturalTarget(l)).forEach(hit ->{
                            if(attackHandler.wasHitWithin(hit, 8)) return;
                            attackHandler.hit(hit);
                            attack(hit);
                        });
                        new ParticleBuilder(Particle.ENTITY_EFFECT)
                            .speed(.05)
                            .offset(box.getWidthX()/2, box.getHeight()/2,box.getWidthZ()/2)
                            .count(CruxMath.random(3, 5))
                            .color(Color.fromRGB(0x70818A))
                            .location(getMouthPos())
                            .spawn()
                        ;
                        new ParticleBuilder(Particle.SNEEZE)
                            .speed(.05)
                            .location(getMouthPos())
                            .spawn()
                        ;
                    }
                }

                @Override
                public int getHitTime() {
                    return 0;
                }
            }
        ), List.of(
            new MobAttack() {
                @Override
                public String getAnimationID() {
                    return "attack_throw_1";
                }

                @Override
                public int getHitTime() {
                    return 0;
                }

                @Override
                public int getCooldown() {
                    return CruxMath.random(50, 90);
                }

                boolean shot = false;
                @Override
                public void onUse() {
                    MobAttack.super.onUse();
                    CreateSound.sound(Sound.ENTITY_SLIME_SQUISH, 1.5f).playAt(mob);
                }

                @Override
                public void onFinish() {
                    MobAttack.super.onFinish();
                    shot = false;
                }

                @Override
                public void onTick() {
                    MobAttack.super.onTick();

                    int time = getPlayingAnimationTimeTicks(getAnimationID());
                    if(!shot && time >= 22 && time <= 25){
                        shot = true;
                        shoot();
                        CreateSound.sound(Sound.ENTITY_LLAMA_SPIT, 1.8f).playAt(mob);
                    }
                }

                @Override
                public boolean canUseAttack() {
                    if(target == null) return false;
                    if(!target.getWorld().equals(mob.getWorld())) return false;
                    double distance = getSquaredDistanceFromTarget();
                    double range = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE);

                    double check = range * 2.5D;
                    if(distance > (check*check)){
                        return true;
                    }
                    return false;
                }

                public void shoot(){
                    Location spawn = mob.getEyeLocation();
                    if(target != null){
                        spawn = CruxLoc.lookAt(spawn, target.getLocation().add(0, target.getHeight()/2, 0));
                    }
                    Vector dir = spawn.getDirection();
                    dir.multiply(CruxMath.random(1.5f, 2f));

                    spawn.getWorld().spawn(spawn, Snowball.class, e ->{
                        e.setItem(new ItemStack(Material.PHANTOM_MEMBRANE));
                        CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(7D));
                        CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(7D));
                        e.setVelocity(dir);

                        EntityMemory.getOrCreateDataHolder(e, SpettleProjectileHolder.class, SpettleProjectileHolder::new);
                    });

                    int amount = CruxMath.random(1, 3);
                    while(amount > 0){
                        amount--;
                        Vector v = dir.clone();
                        v.multiply(CruxMath.random(1.5f, 2f));
                        applyInaccuracy(v, 5D, 3D);

                        spawn.getWorld().spawn(spawn, Snowball.class, e ->{
                            e.setItem(new ItemStack(Material.PHANTOM_MEMBRANE));
                            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(7D));
                            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(7D));
                            e.setVelocity(v);

                            EntityMemory.getOrCreateDataHolder(e, SpettleProjectileHolder.class, SpettleProjectileHolder::new);
                        });
                    }
                }
            }
        ));
    }

    public Vector applyInaccuracy(Vector direction, double maxYawDegrees, double maxPitchDegrees) {
        Random random = new Random();

        // Convert degrees to radians
        double yawRadians = Math.toRadians((random.nextDouble() - 0.5) * 2 * maxYawDegrees);
        double pitchRadians = Math.toRadians((random.nextDouble() - 0.5) * 2 * maxPitchDegrees);

        // Apply pitch (around X-axis)
        direction.rotateAroundX(pitchRadians).rotateAroundY(yawRadians);

        return direction.normalize();
    }

    public Location getLeftHandPos(){
        return getModel().getBone("left_hand_pos").get().getLocation();
    }

    public Location getRightHandPos(){
        return getModel().getBone("right_hand_pos").get().getLocation();
    }

    public Location getMouthPos(){
        return getModel().getBone("mouth_pos").get().getLocation();
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
