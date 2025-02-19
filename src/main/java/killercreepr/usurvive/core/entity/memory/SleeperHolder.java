package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.util.Pair;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.usurvive.api.entity.sleeper.SlumberData;
import killercreepr.usurvive.api.event.EntityDeepSlumberEvent;
import killercreepr.usurvive.api.event.EntityLeaveSlumberEvent;
import killercreepr.usurvive.api.event.EntitySlumberEvent;
import killercreepr.usurvive.api.values.SleeperCfg;
import killercreepr.usurvive.core.entity.sleeper.BedSlumberData;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeakness;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessWarningData;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.key.Key;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SleeperHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("sleeper");
    protected final Holder<SleeperCfg> cfgHolder;
    public SleeperHolder(@NotNull Key key, @NotNull EntityMemory parent, Holder<SleeperCfg> cfg) {
        super(key, parent);
        this.cfgHolder = cfg;
    }
    public SleeperHolder(@NotNull EntityMemory parent, Holder<SleeperCfg> cfg) {
        this(KEY, parent, cfg);
    }

    public SleeperCfg cfg(){
        return cfgHolder.value();
    }

    protected SlumberData slumberData;
    protected int ticksSleeping = 0;
    protected long lastSlept;
    protected long lastAwoken;
    protected int slumberTicks;
    protected int ticksAwake = 0;
    protected int sleepWeakness = 0;
    protected long timeStartedBeforeDeepSleep = -1;

    public long getTimeBetweenDeepSleep(long currentWorldTime){
        return currentWorldTime - timeStartedBeforeDeepSleep;
    }

    public long getTimeStartedBeforeDeepSleep() {
        return timeStartedBeforeDeepSleep;
    }

    public void setTimeStartedBeforeDeepSleep(long timeStartedBeforeDeepSleep) {
        this.timeStartedBeforeDeepSleep = timeStartedBeforeDeepSleep;
    }

    public boolean processingData(){
        return ticksSleeping == -1;
    }

    @Override
    public void onMemoryUnload(@NotNull Entity e) {
        super.onMemoryUnload(e);
        sleep(null);
    }

    protected int lastWarningIndex = -1;
    public void updateSleepWeaknessTick(int tick){
        if(tick % 2 != 0) return;
        sleepWeakness = calculateSleepWeaknessLevel(slumberTicks);
        var warning = calculateSleepWeaknessWarning(slumberTicks);
        if(warning != null){
            if (warning.getSecond() > lastWarningIndex) {
                warning.getFirst().getMsg().use(parent.value());
            }
            lastWarningIndex = warning.getSecond();
        }
        if(sleepWeakness < 1) lastWarningIndex = -1;
    }

    public void sleepWeaknessTick(int level){
        SleepWeakness effects = cfg().SLEEP_WEAKNESS_LEVELS().valueOr(Map.of()).get(level);
        if(effects==null) return;
        if(!(parent.value() instanceof Player p)) return;
        Crux.scheduler().runTask(() ->{
            if(!CruxEntityUtil.isValid(p)) return;
            p.addPotionEffects(effects.getPotions());
        });
    }

    @Override
    public void tick(@NotNull Entity entity) {
        if(processingData()) return;
        if(isSleeping()){
            sleepTick(entity);
            return;
        }
        nonSleepTick();
    }

    public boolean isDeepSleeping(){
        return ticksSleeping >= cfg().DEEP_SLEEP_START_TIME().value().intValue();
    }

    public Pair<SleepWeaknessWarningData, Integer> calculateSleepWeaknessWarning(int slumberTicks){
        var map = cfg().SLEEP_WEAKNESS_WARNINGS().value();
        if(map == null) return null;
        return map.calculateWarningData(slumberTicks);
    }

    public int calculateSleepWeaknessLevel(int slumberTicks){
        var map = cfg().SLEEP_WEAKNESS_LEVELS_DEFINITION().value();
        if(map == null) return 0;
        return map.calculateSleepWeaknessLevel(slumberTicks);
    }

    public void setSlumberTicks(int slumberTicks) {
        this.slumberTicks = Math.max(slumberTicks, 0);
    }

    public void nonSleepTick(){
        ticksAwake++;
        if(slumberTicks < 1){
            return;
        }
        SleeperCfg cfg = cfg();
        if(ticksAwake % cfg.SLEEP_WEAKNESS_SLUMBER_DROP_OFF().value().intValue() == 0){
            setSlumberTicks(slumberTicks - cfg.SLEEP_WEAKNESS_SLUMBER_DROP_OFF_AMOUNT().value().intValue());
        }
        if(sleepWeakness > 0){
            updateSleepWeaknessTick(ticksAwake);
            sleepWeaknessTick(sleepWeakness);
        }
    }

    public void scheduleForAwaken(){
        if(!isSleeping()) return;
        ticksSleeping = -1;
        Crux.scheduler().runTask(() ->{
            sleep(null);
            ticksSleeping = 0;
        });
    }

    public void sleepTick(Entity e){
        ticksAwake = 0;
        if(slumberData != null && slumberData.shouldStop()){
            scheduleForAwaken();
            return;
        }
        ticksSleeping++;
        if(slumberData != null){
            slumberData.tick();
        }
        slumberTicks++;
        updateSleepWeaknessTick(ticksSleeping);
        if(sleepWeakness < 1){
            sleepTickNotWeak();
        }

        sleepMsgTick(e);

        if(isDeepSleeping() && !deepSleepLastTick){
            deepSleepLastTick = true;
            EntityDeepSlumberEvent event = new EntityDeepSlumberEvent(e, slumberData);
            event.callEvent();
            setTimeStartedBeforeDeepSleep(e.getWorld().getFullTime());
        }else if(isDeepSleeping()){
            long timePassed = getTimeBetweenDeepSleep(e.getWorld().getFullTime());
            if(timePassed >= cfg().AUTO_KICK_SLEEPER_AFTER_TIME_PASSED().value().intValue()){
                scheduleForAwaken();
            }
        }
    }

    public void sleepMsgTick(Entity e){
        if(isDeepSleeping()){
            Lang.DEEP_SLEEPING_TICK.use(e, TagContainer.merged().hook(e));
        }else{
            Lang.SLEEPING_TICK.use(e, TagContainer.merged().hook(e));
        }
    }

    protected int tickHealth;
    protected int tickHunger;
    protected boolean deepSleepLastTick = false;

    public void sleepTickNotWeak(){
        SleeperCfg cfg = cfg();
        if(!isDeepSleeping()) return;
        if(!(parent.value() instanceof HumanEntity p)) return;

        if(tickHealth == 0){
            tickHealth = ticksSleeping + cfg.SLEEP_REGENERATION_RATE().value().intValue();
        }
        if(p.getHealth() > 0D && p.getFoodLevel() > 0 && ticksSleeping > tickHealth){
            tickHealth = ticksSleeping + cfg.SLEEP_REGENERATION_RATE().value().intValue();
            double hp = cfg.SLEEP_REGENERATION_AMOUNT().value().doubleValue();
            double newHP = CruxMath.clamp(p.getHealth() + hp, 0D, p.getAttribute(Attribute.MAX_HEALTH).getValue());
            if(newHP != p.getHealth()){
                p.setHealth(newHP);
            }
        }

        if(tickHunger == 0){
            tickHunger = ticksSleeping + cfg.SLEEP_HUNGER_SATURATION_RATE().value().intValue();
        }
        if(p.getSaturation() > 0f && ticksSleeping > tickHunger){
            tickHunger = ticksSleeping + cfg.SLEEP_HUNGER_SATURATION_RATE().value().intValue();
            float value = cfg.SLEEP_HUNGER_SATURATION_AMOUNT().value().floatValue();
            float newValue = CruxMath.clamp(p.getSaturation() + value, 0f, 20f);
            if(p.getSaturation() != newValue) p.setSaturation(newValue);
            return;
        }

        if(tickHunger == 0){
            tickHunger = ticksSleeping + cfg.SLEEP_HUNGER_RATE().value().intValue();
        }
        if(p.getFoodLevel() > 0 && ticksSleeping > tickHunger){
            tickHunger = ticksSleeping + cfg.SLEEP_HUNGER_RATE().value().intValue();
            int value = cfg.SLEEP_HUNGER_AMOUNT().value().intValue();
            int newValue = CruxMath.clamp(p.getFoodLevel() + value, 0, 20);
            if(p.getFoodLevel() != newValue) p.setFoodLevel(newValue);
        }
    }

    public int getTicksSleeping() {
        return ticksSleeping;
    }

    public void onAwaken(SlumberData from){
        lastAwoken = System.currentTimeMillis();
        tickHealth = 0;
        tickHunger = 0;
        deepSleepLastTick = false;
    }

    public void onSleep(SlumberData to){
        lastSlept = System.currentTimeMillis();
        tickHealth = 0;
        tickHunger = 0;
        deepSleepLastTick = false;
        if(!(parent.value() instanceof Player p)) return;
        if(to instanceof BedSlumberData data){
            p.incrementStatistic(Statistic.SLEEP_IN_BED);
            if(data.shouldSetBedLocation()){
                p.setRespawnLocation(data.getLocation());
            }
        }
    }

    public SlumberData getSlumberData() {
        return slumberData;
    }

    public boolean isSleeping() {
        return slumberData != null;
    }

    public boolean sleep(SlumberData slumberData, boolean silentSlumber, boolean silentLeave) {
        if(slumberData == this.slumberData) return false;
        if(this.slumberData != null){

            if(!silentLeave){
                EntityLeaveSlumberEvent leaveEvent = new EntityLeaveSlumberEvent(parent.value(), this.slumberData);
                if(!leaveEvent.callEvent()) return false;
            }

            this.slumberData.stopped();
            onAwaken(slumberData);
        }

        ticksSleeping = 0;
        if(slumberData != null){
            if(!silentSlumber){
                EntitySlumberEvent slumberEvent = new EntitySlumberEvent(parent.value(), slumberData);
                if(!slumberEvent.callEvent()) return false;
                slumberData = slumberEvent.getSlumber();
            }
            slumberData.started();
        }
        this.slumberData = slumberData;
        onSleep(slumberData);
        return true;
    }

    public boolean sleep(SlumberData slumberData) {
        return sleep(slumberData, false, false);
    }
}
