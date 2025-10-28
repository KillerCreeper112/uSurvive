package killercreepr.usurvive.core.entity.mob.goals;

import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.core.model.ModeledEntityImpl;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.Crux;
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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class DownmanGoal extends CruxMobModeledGoal {
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
            }
        ), List.of());
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
        playAnimation(id, true);
    }

    public String generateAttackAnimationID(){
        return "attack";
    }

    @Override
    public void tick() {
        super.tick();
        attackHandler.tick();

        crawlCheckTick();
        animationPropertyTick();

        double moveSpeed = CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED);
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(moveSpeed);
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

    public boolean shouldRun(){
        return target != null;
    }

    public boolean isPlayingCorrectRunAnimation(){
        String current = getCurrentRunAnimation();
        String should = getRunAnimation();
        return Objects.equals(current, should);
    }

    public boolean isRunning(){
        return getCurrentRunAnimation() != null;
    }

    public @Nullable String getCurrentRunAnimation(){
        if(isPlayingAnimation("crawl_run")) return "crawl_run";
        if(isPlayingAnimation("run")) return "run";
        return null;
    }

    public @NotNull String getRunAnimation(){
        if(isCrawling()) return "crawl_run";
        return "run";
    }

    public int getAttackTimeMax(){
        if(isCrawling()) return 2;//4
        return 3;
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

    protected BoundingBox beforeBox;
    protected double beforeEyeHeight;
    public void setCrawling(boolean crawl){
        if(crawl){
            if(isCrawling()) return;
            playAnimation("stand_to_crawl", true);
            BoundingBox box = mob.getBoundingBox();
            beforeBox = box.clone();
            beforeEyeHeight = mob.getEyeHeight();
            double width = box.getWidthX();
            double depth = box.getWidthZ();
            double height = box.getHeight() * 0.3;
            double eye = mob.getEyeHeight() * 0.3;
            Hitbox hitbox = new Hitbox(width, height, depth, eye);
            getModeledEntity().getBase().getData().setCullHitbox(
                hitbox
            );
            ModelEngineAPI.getEntityHandler().setHitbox(
                mob,
                hitbox
            );
            return;
        }
        if(!isCrawling()) return;
        stopAnimation("crawl");
        stopAnimation("stand_to_crawl");
        if(beforeBox != null){
            Hitbox hitbox = new Hitbox(
                beforeBox.getWidthX(),
                beforeBox.getHeight(),
                beforeBox.getWidthZ(),
                beforeEyeHeight
            );
            getModeledEntity().getBase().getData().setCullHitbox(
                hitbox
            );
            ModelEngineAPI.getEntityHandler().setHitbox(
                mob,
                hitbox
            );
            beforeBox = null;
        }
    }

    public boolean isAttacking(){
        return getCurrentAttackAnimation() != null;
    }

    public boolean isCrawling(){
        return isPlayingAnimation("stand_to_crawl") || isPlayingAnimation("crawl");
    }

    public boolean shouldCrawl(){
        Block b = mob.getLocation().add(0, 1, 0).getBlock();
        if(b.isSolid()) return true;

        Pathfinder.PathResult path = mob.getPathfinder().getCurrentPath();
        if(path==null) return false;
        Location end = path.getFinalPoint();
        if(end==null) return false;
        Block endBlock = end.getBlock();
        if(endBlock.getRelative(BlockFace.UP).isSolid()) return true;
        return false;
    }


    protected final AnimationHandler.DefaultProperty WALK_NORMAL = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "walk", .2D, .2D, 1D
    );
    protected final AnimationHandler.DefaultProperty WALK_RUN = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "run", .2D, .2D, 1D
    );
    protected final AnimationHandler.DefaultProperty WALK_CRAWL_RUN = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "crawl_run", .2D, .2D, 1D
    );
    public AnimationHandler.DefaultProperty getDefaultWalk(){
        if(target != null){
            if(isCrawling()) return WALK_CRAWL_RUN;
            return WALK_RUN;
        }
        return WALK_NORMAL;
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
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return super.isValidNaturalTarget(target) && !CruxMob.isInCategory(target, MobCategory.ENEMY);
    }
}
