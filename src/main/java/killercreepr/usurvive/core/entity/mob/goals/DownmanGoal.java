package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttackHandler;
import killercreepr.usurvive.core.entity.mob.goals.data.StrongMobAttack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DownmanGoal extends CruxMobModeledGoal implements Listener {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("downman"));

    protected final MobAttackHandler attackHandler;
    public DownmanGoal(@NotNull Mob mob) {
        this(KEY, mob);
    }

    public DownmanGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);

        attackHandler = new MobAttackHandler(mob, this, List.of(
            new StrongMobAttack(1) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.05D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public int getHitTime() {
                    return 6;
                }

                @Override
                public String getAnimationID() {
                    return isCrawling() ? "crawl_attack" : "attack";
                }
            }
        ), List.of()){
            @Override
            public int calculateStrongAttackCooldown() {
                return 0;
            }

            @Override
            public boolean canUseStrongAttack() {
                return super.canUseStrongAttack() && wasAlertLastTick && isAlert() && !isScreaming();
            }
        };
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        playAnimation("hurt", true);
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
        //playAnimation(id, true);
    }

    public String generateAttackAnimationID(){
        return "attack";
    }

    protected short manualAlertTicks;
    protected short darkSpotFindCooldown;

    @Override
    protected int getFindCooldownMax() {
        if(isAlert()) return 25;
        return 10;
    }

    public int getDarkSpotDistance(){
        return 10;
    }

    public int getDarkSpotYDistance(){
        return 4;
    }

    public boolean canBeAt(Block b){
        if(!b.isPassable() && !b.isEmpty()) return false;
        Block ground = b.getRelative(BlockFace.DOWN);
        if(!ground.isSolid() || ground.isLiquid()) return false;

        int height = (int) Math.ceil(mob.getHeight()) - 1;
        for(int i = 1; i <= height; i++){
            Block check = b.getRelative(0, i, 0);
            if(!check.isPassable() && !check.isEmpty()) return false;
        }
        return true;
    }

    public Block findDarkSpot(Block origin) {
        int searchRadius = getDarkSpotDistance();
        int searchYRadius = getDarkSpotYDistance();

        for (int i = 0; i < 30; i++) { // Try 30 random spots
            int x = CruxMath.random(-searchRadius, searchRadius);
            int y = CruxMath.random(-searchYRadius, searchYRadius);
            int z = CruxMath.random(-searchRadius, searchRadius);
            Block check = origin.getRelative(x, y, z);
            if(check.getLightLevel() < 1 && canBeAt(check)){
                return check;
            }
        }
        return null;
    }

    @Override
    public boolean canAttack() {
        return super.canAttack() && (wasAlertLastTick && isAlert()) && !isScreaming();
    }

    protected boolean wasAlertLastTick;
    protected long screamed;
    @Override
    public void tick() {
        wasAlertLastTick = isAlert();
        this.checkTargetLogic();
        idleTick();
        this.targetLogic();
        this.sound();
        if(!isScreaming()){
            attackHandler.tick();
        }

        crawlCheckTick();
        animationPropertyTick();
    }

    public boolean isScreaming(){
        return CruxMath.hasOccurredWithin(screamed, 5) || isPlayingAnimation("scream");
    }

    public void scream(){
        screamed = System.currentTimeMillis();
        playAnimation("scream", true);
    }

    public short getManualAlertTicks() {
        return manualAlertTicks;
    }

    public void setManualAlertTicks(short manualAlertTicks) {
        this.manualAlertTicks = manualAlertTicks;
    }

    public void idleTick(){
        if(manualAlertTicks > 0){
            if(mob.getPathfinder().getCurrentPath() == null) manualAlertTicks--;
        }
        if(isScreaming()){
            return;
        }

        var followRange = mob.getAttribute(Attribute.FOLLOW_RANGE);
        var moveSpeed = mob.getAttribute(Attribute.MOVEMENT_SPEED);
        if(isAlert()){
            if(!wasAlertLastTick){
                if(target != null){
                    scream();
                    return;
                }
            }

            if(moveSpeed.getModifier(Crux.key("idle")) != null){
                getModeledEntity().setModelRotationLocked(false);
            }
            followRange.removeModifier(Crux.key("idle"));
            moveSpeed.removeModifier(Crux.key("idle"));
            return;
        }
        idleAvoidLightTick();
        if(moveSpeed.getModifier(Crux.key("idle")) != null) return;

        followRange.addTransientModifier(new AttributeModifier(Crux.key("idle"), -0.9D, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        moveSpeed.addTransientModifier(new AttributeModifier(Crux.key("idle"), -5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        getModeledEntity().setModelRotationLocked(true);
    }

    public void idleAvoidLightTick(){
        Block block = mob.getLocation().getBlock();
        byte lightLevel = block.getLightLevel();
        if(lightLevel < 1) return;
        if(darkSpotFindCooldown > 0){
            darkSpotFindCooldown--;
            return;
        }
        darkSpotFindCooldown = (short) CruxMath.random(0, 8);
        Block darkSpot = findDarkSpot(block);
        if(darkSpot == null) return;
        setManualAlertTicks((short) 15);
        Location loc = darkSpot.getLocation().toCenterLocation();
        moveTo(loc);
    }

    public void crawlCheckTick(){
        if(isCrawling()){
            if(!shouldCrawl()){
                setCrawling(false);
                return;
            }
            return;
        }
        if(shouldCrawl()){
            setCrawling(true);
        }
    }

    public @Nullable String getCurrentAttackAnimation(){
        if(isPlayingAnimation("crawl_attack")) return "crawl_attack";
        if(isPlayingAnimation("attack")) return "attack";
        return null;
    }

    public @NotNull String getAttackAnimation(){
        if(isCrawling()) return "crawl_attack";
        return "attack";
    }

    //protected BoundingBox beforeBox;
    //protected double beforeEyeHeight;
    public void setCrawling(boolean crawl){
        if(crawl){
            if(isCrawling()) return;
            playAnimation("stand_to_crawl", true);
            getModel().setHitboxScale(0D);
            getModeledEntity().addModel(
                ModelEngineAPI.createActiveModel("downman_crawl_hitbox"),
                true
            );
            /*BoundingBox box = mob.getBoundingBox();
            beforeBox = box.clone();
            beforeEyeHeight = mob.getEyeHeight();
            double width = box.getWidthX();
            double depth = box.getWidthZ();
            double height = box.getHeight() * 0.3;
            double eye = mob.getEyeHeight() * 0.3;
            Vector3fc scale = getModel().getScale();
            Hitbox hitbox = new Hitbox(width * scale.x(), height * scale.y(), depth * scale.z(), eye * scale.y());

            getModel().setMainHitbox(false);
            getModel().setHitboxScale(0D);
            getModeledEntity().getBase().getData().setCullHitbox(hitbox);

            ModelEngineAPI.getEntityHandler().setHitbox(
                mob,
                hitbox
            );*/
            return;
        }
        if(!isCrawling()) return;
        stopAnimation("crawl");
        stopAnimation("stand_to_crawl");
        getModel().setHitboxScale(1D);
        getModeledEntity().removeModel("downman_crawl_hitbox").ifPresent(ActiveModel::destroy);
        /*if(beforeBox != null){
            Hitbox hitbox = new Hitbox(
                beforeBox.getWidthX(),
                beforeBox.getHeight(),
                beforeBox.getWidthZ(),
                beforeEyeHeight
            );
            getModel().setMainHitbox(true);
            getModel().setHitboxScale(1D);
            getModeledEntity().getBase().getData().setCullHitbox(hitbox);
            ModelEngineAPI.getEntityHandler().setHitbox(
                mob,
                hitbox
            );
            beforeBox = null;
        }*/
    }

    public boolean isAttacking(){
        return getCurrentAttackAnimation() != null;
    }

    public boolean isCrawling(){
        return isPlayingAnimation("stand_to_crawl") || isPlayingAnimation("crawl");
    }

    public boolean isAlert(){
        return manualAlertTicks > 0 || lastKnownTargetLocation != null || target != null;
    }

    public boolean shouldCrawl(){
        Block b = mob.getLocation().add(0, 1, 0).getBlock();
        if(b.isSolid()) return true;

        if(target != null){
            b = target.getLocation().add(0, 1, 0).getBlock();
            if(b.isSolid()) return true;
        }

        Pathfinder.PathResult path = mob.getPathfinder().getCurrentPath();
        if(path==null) return false;
        Location end = path.getFinalPoint();
        if(end==null) return false;
        Block endBlock = end.getBlock();
        if(endBlock.getRelative(BlockFace.UP).isSolid()) return true;
        return false;
    }

    protected final AnimationHandler.DefaultProperty IDLE_NORMAL = new AnimationHandler.DefaultProperty(
        ModelState.IDLE, "idle", .2D, .2D, 1D
    );
    protected final AnimationHandler.DefaultProperty IDLE_ALERT = new AnimationHandler.DefaultProperty(
        ModelState.IDLE, "idle_alert", .2D, .2D, 1D
    );

    protected final AnimationHandler.DefaultProperty WALK_NORMAL = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "walk", .2D, .2D, 1D
    );
    protected final AnimationHandler.DefaultProperty WALK_RUN = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "run", .2D, .2D, 1D
    );
    protected final AnimationHandler.DefaultProperty WALK_CRAWL_RUN = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "crawl_run", .2D, .2D, 1D
    );
    public AnimationHandler.DefaultProperty getDefaultState(ModelState state){
        switch (state){
            case WALK -> {
                if(target != null){
                    if(isCrawling()) return WALK_CRAWL_RUN;
                    return WALK_RUN;
                }
                return WALK_NORMAL;
            }
            case IDLE -> {
                if(isAlert()) return IDLE_ALERT;
                return IDLE_NORMAL;
            }
        }
        return null;
    }

    public static final Collection<ModelState> CHECK_STATES = Set.of(
        ModelState.WALK,
        ModelState.IDLE
    );
    public void animationPropertyTick(){
        ActiveModel model = getModel();
        if(model ==null) return;
        for (ModelState state : CHECK_STATES) {
            AnimationHandler.DefaultProperty current = model.getAnimationHandler().getDefaultProperty(state);
            AnimationHandler.DefaultProperty should = getDefaultState(state);
            if(!current.equals(should)){
                stopAnimation(current.getAnimation());
                model.getAnimationHandler().setDefaultProperty(should);
                playAnimation(should.getAnimation(), false);
            }
        }
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return super.isValidNaturalTarget(target) && !CruxMob.isInCategory(target, MobCategory.ENEMY);
    }

    public double getBlockUpdatesRange(){
        return 8D;
    }

    public boolean checkBlock(Block b, LivingEntity e){
        if(hasTarget()) return false;

        if(!isValidNaturalTarget(e)) return false;
        double distance = b.getLocation().toCenterLocation().distanceSquared(mob.getLocation());
        if(distance > getBlockUpdatesRange()) return false;

        setTarget(e);
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) return;
        if(!event.getAction().isRightClick()) return;
        if(event.useInteractedBlock() == Event.Result.DENY) return;
        checkBlock(event.getClickedBlock(), event.getPlayer());
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        checkBlock(event.getBlock(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        checkBlock(event.getBlock(), event.getPlayer());
    }
}
