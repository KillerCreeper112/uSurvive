package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.usurvive.api.entity.sleeper.SlumberData;
import killercreepr.usurvive.api.event.EntityLeaveSlumberEvent;
import killercreepr.usurvive.api.event.EntitySlumberEvent;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SleeperHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("sleeper");
    public SleeperHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }
    public SleeperHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    protected SlumberData slumberData;
    protected int ticksSleeping = 0;

    public boolean processingData(){
        return ticksSleeping == -1;
    }

    @Override
    public void tick(@NotNull Entity entity) {
        if(processingData()) return;
        if(isSleeping()){
            if(slumberData != null && slumberData.shouldStop()){
                ticksSleeping = -1;
                Crux.scheduler().runTask(() ->{
                    sleep(null);
                    ticksSleeping = 0;
                });
                return;
            }
            ticksSleeping++;
            if(slumberData != null) slumberData.tick();
        }
    }

    public int getTicksSleeping() {
        return ticksSleeping;
    }

    public SlumberData getSlumberData() {
        return slumberData;
    }

    public boolean isSleeping() {
        return slumberData != null;
    }

    public boolean sleep(SlumberData slumberData, boolean silentSlumber, boolean silentLeave) {
        if(this.slumberData != null){

            if(!silentLeave){
                EntityLeaveSlumberEvent leaveEvent = new EntityLeaveSlumberEvent(parent.value(), this.slumberData);
                if(!leaveEvent.callEvent()) return false;
            }

            this.slumberData.stopped();
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
        return true;
    }

    public boolean sleep(SlumberData slumberData) {
        return sleep(slumberData, false, false);
    }
}
