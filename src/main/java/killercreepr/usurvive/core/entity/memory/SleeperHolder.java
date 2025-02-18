package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.util.Pair;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.usurvive.api.entity.sleeper.SlumberData;
import killercreepr.usurvive.api.event.EntityLeaveSlumberEvent;
import killercreepr.usurvive.api.event.EntitySlumberEvent;
import killercreepr.usurvive.api.values.SleeperCfg;
import killercreepr.usurvive.core.entity.sleeper.BedSlumberData;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeakness;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessWarningData;
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
            sleepTick();
            return;
        }
        nonSleepTick();
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

    public void sleepTick(){
        ticksAwake = 0;
        if(slumberData != null && slumberData.shouldStop()){
            ticksSleeping = -1;
            Crux.scheduler().runTask(() ->{
                sleep(null);
                ticksSleeping = 0;
            });
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
    }
    public void sleepTickNotWeak(){
        SleeperCfg cfg = cfg();
        if(ticksAwake < cfg.DEEP_SLEEP_START_TIME().value().intValue()) return;
        if(!(parent.value() instanceof HumanEntity p)) return;

        if(p.getHealth() > 0D && ticksAwake % cfg.SLEEP_REGENERATION_RATE().value().intValue() == 0){
            double hp = cfg.SLEEP_REGENERATION_AMOUNT().value().doubleValue();
            double newHP = CruxMath.clamp(
                p.getHealth() + hp, 0D, p.getAttribute(Attribute.MAX_HEALTH).getValue()
            );
            if(newHP != p.getHealth()){
                p.setHealth(newHP);
            }
        }

        if(p.getSaturation() > 0f && ticksAwake % cfg.SLEEP_HUNGER_SATURATION_RATE().value().intValue() == 0){
            float value = cfg.SLEEP_HUNGER_SATURATION_AMOUNT().value().floatValue();
            float newValue = CruxMath.clamp(p.getSaturation() + value, 0f, 20f);
            if(p.getSaturation() != newValue) p.setSaturation(newValue);
            return;
        }

        if(p.getFoodLevel() > 0 && ticksAwake % cfg.SLEEP_HUNGER_RATE().value().intValue() == 0){
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
    }

    public void onSleep(SlumberData to){
        lastSlept = System.currentTimeMillis();
        if(!(parent.value() instanceof Player p)) return;
        if(to instanceof BedSlumberData){
            p.incrementStatistic(Statistic.SLEEP_IN_BED);
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
