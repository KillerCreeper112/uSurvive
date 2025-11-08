package killercreepr.usurvive.core.entity.mob.goal.path;

import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalNode;
import killercreepr.usurvive.api.entity.mob.goal.path.GuidePathGoalNode;
import killercreepr.usurvive.api.guide.GuidePoint;
import org.bukkit.entity.Entity;

public class WrappedGuidePathGoalNode implements GuidePathGoalNode {
    protected final GoalNode goalNode;
    protected final GuidePoint guideGoal;

    public WrappedGuidePathGoalNode(GoalNode goalNode, GuidePoint guideGoal) {
        this.goalNode = goalNode;
        this.guideGoal = guideGoal;
    }

    @Override
    public GuidePoint guidePoint() {
        return guideGoal;
    }

    @Override
    public double x() {
        return goalNode.x();
    }

    @Override
    public double y() {
        return goalNode.y();
    }

    @Override
    public double z() {
        return goalNode.z();
    }

    @Override
    public boolean canMoveOn(Entity entity) {
        return goalNode.canMoveOn(entity);
    }
}
