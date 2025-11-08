package killercreepr.usurvive.api.entity.mob.goal.path;

import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalNode;
import killercreepr.usurvive.api.guide.GuidePoint;
import killercreepr.usurvive.core.entity.mob.goal.path.WrappedGuidePathGoalNode;
import killercreepr.usurvive.core.entity.mob.goals.guide.GuidePathGoal;

public interface GuidePathGoalNode extends GoalNode {
    static GuidePathGoalNode guidePathGoalNode(GoalNode node, GuidePoint guide){
        return new WrappedGuidePathGoalNode(node, guide);
    }

    GuidePoint guidePoint();

    @Override
    default void onFinish(PathTargetMobGoal goal) {
        GoalNode.super.onFinish(goal);
        if(goal instanceof PathTargetMobGoal.Goaled gg && gg.getGoal() instanceof GuidePathGoal g){
            g.setNextGuidePoint(guidePoint());
        }
    }
}
