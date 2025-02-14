package killercreepr.usurvive.core.world.module;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.data.tick.Ticked;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
import killercreepr.cruxworlds.core.world.module.SimpleWorldModule;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class ResetWorldModule extends SimpleWorldModule implements Ticked {
    protected final Holder<NumberProvider> resetTime;
    protected final CruxWorldType type;
    public ResetWorldModule(@NotNull CruxWorld parent, Holder<NumberProvider>  resetTime, CruxWorldType type) {
        super(parent);
        this.resetTime = resetTime;
        this.type = type;
    }
    protected int tick = 0;
    protected boolean resetting = false;
    //1 hour = 72,000
    @Override
    public void tick() {
        if(resetting) return;
        tick++;
        //after 30 seconds of init or every hour, check.
        if(tick == 600 || tick >= 72000){
            if(tick != 600) tick = 600;
            check();
        }
    }

    public long getLastReset(){
        return CruxTag.get(parent.toBukkitWorld(), "last_reset", PersistentDataType.LONG, 0L);
    }

    public void check(){
        int resetTime = this.resetTime.value().value().intValue();
        long lastReset = getLastReset();
        if(CruxMath.hasOccurredWithin(lastReset, resetTime)) return;
        resetting = true;
        Crux.scheduler().runTask(() -> reset());
    }

    public void reset(){
        CruxWorldManager worldManager = CruxCore.core().worldManager();
        Crux.log(Level.INFO, "[ResetWorldModule] Resetting world, " + parent.getName() + "...");
        worldManager.deleteWorld(parent).whenComplete((value, throwable) ->{
            if(throwable != null) Crux.log(Level.SEVERE, throwable.getMessage());
            if(!value){
                Crux.log(Level.SEVERE, "[ResetWorldModule] COULD NOT DELETE " + parent.getName() + ".");
                return;
            }
            CruxWorld created = worldManager.getOrCreateWorld(type);
            if(created != null){
                CruxTag.set(created.toBukkitWorld(), "last_reset", PersistentDataType.LONG, System.currentTimeMillis());
                Crux.log(Level.INFO, "[ResetWorldModule] World, " + parent.getName() + " has been reset.");
            }else{
                Crux.log(Level.SEVERE, "[ResetWorldModule] COULD NOT CREATE NEW " + parent.getName() + ".");
            }
        });
    }
}
