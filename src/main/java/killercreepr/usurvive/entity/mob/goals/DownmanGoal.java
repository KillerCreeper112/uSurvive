package killercreepr.usurvive.entity.mob.goals;

import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import io.papermc.paper.event.entity.EntityMoveEvent;
import killercreepr.crux.Crux;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DownmanGoal extends CruxMobModeledGoal {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("downman"));

    public DownmanGoal(@NotNull Mob mob) {
        this(KEY, mob);
    }

    public DownmanGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        playAnimation("hurt", true);
    }

    protected int attackTime = 0;
    @Override
    public boolean preAttemptAttack() {
        if(attackTime < 1){
            attackTime = getAttackTimeMax();
            playAnimation(getAttackAnimation(), true);
            return false;
        }
        return super.preAttemptAttack();
    }

    public void attackTick(){
        if(attackTime < 1) return;
        attackTime--;
        if(attackTime < 1){
            attemptAttack();
        }
    }

    @Override
    public void tick() {
        super.tick();
        attackTick();

        crawlCheckTick();

        double moveSpeed = CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED);
        mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(moveSpeed);
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

    public void setRunning(boolean run){
        String current = getCurrentRunAnimation();
        String should = getRunAnimation();
        if(Objects.equals(current, should)) return;
        if(current != null) stopAnimation(current);
        if(run){
            playAnimation(should, true);
            return;
        }
        if(current==null) return;
        stopAnimation(current);
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

    public void setCrawling(boolean crawl){
        if(crawl){
            if(isCrawling()) return;
            playAnimation("stand_to_crawl", true);
            return;
        }
        if(!isCrawling()) return;
        stopAnimation("crawl");
        stopAnimation("stand_to_crawl");
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
        //Bukkit.broadcastMessage(endBlock.getRelative(BlockFace.UP) + " ");
        if(endBlock.getRelative(BlockFace.UP).isSolid()) return true;
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityMove(EntityMoveEvent event) {
        if(!event.getEntity().equals(mob)) return;
        if(!event.hasExplicitlyChangedPosition() || !shouldRun()){
            setRunning(false);
            return;
        }
        setRunning(true);
    }
}
