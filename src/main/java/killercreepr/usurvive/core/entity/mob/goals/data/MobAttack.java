package killercreepr.usurvive.core.entity.mob.goals.data;

public interface MobAttack {
    String getAnimationID();
    default void onUse(){};
    int getHitTime();
    default void onFinish(){}
    default void onTick(){}
    default int getCooldown(){
        return -1;
    }

    default boolean ignoreAnimation(){
        return false;
    }
    default boolean isFinished(){
        return false;
    }

    default boolean canUseAttack(){
        return true;
    }
}
