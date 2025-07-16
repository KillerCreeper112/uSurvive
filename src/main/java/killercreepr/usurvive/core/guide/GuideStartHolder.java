package killercreepr.usurvive.core.guide;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.core.entity.memory.AdvancementHolder;
import killercreepr.cruxadvancements.core.registries.AdvancementRegistries;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.advancement.objective.GuideDiscoverMeteorObjective;
import killercreepr.usurvive.core.advancement.objective.GuideShardChooseObjective;
import killercreepr.usurvive.core.data.DeathGuide;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GuideStartHolder extends PlayerTickedDataHolder {
    public static final Key DISCOVER_GUIDE_METEOR = USurvivePlugin.inst().key("main/discover_guide_meteor");
    public static final Key KEY = Crux.key("guide/start");
    public GuideStartHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
    }
    public GuideStartHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    protected Location fallenMeteorLocation;

    public Location getFallenMeteorLocation() {
        return fallenMeteorLocation;
    }

    public void setFallenMeteorLocation(Location fallenMeteorLocation) {
        this.fallenMeteorLocation = fallenMeteorLocation;
    }

    public DataFile getSaveFile(boolean createIfNeeded){
        return BukkitDataFile.parseFromGeneralPath(
            new CruxFolder(USurvivePlugin.inst(), "data/guide/player/" + parent.getUUID()).file(),
            createIfNeeded
        );
    }

    public boolean deleteSaveFile(){
        DataFile file = getSaveFile(false);
        if(file == null) return false;
        return file.delete();
    }

    @Override
    protected void onTick(@NotNull Player p) {
        super.onTick(p);
        int every = 30;//cfg.DEATH_GUIDE_TICK_EVERY().value().intValue();
        if(every==0) return;
        if(Crux.getCurrentTick() % every != 0) return;
        tickDistance(p);
        tickStructureGuide(p);
    }

    protected double lastDistanceSquared;
    public void tickDistance(Player p){
        if(fallenMeteorLocation == null) return;
        CruxAdvancementManager manager = AdvancementRegistries.ADVANCEMENT_MANAGERS.get(USurvivePlugin.inst().key("main"));
        if(manager == null) return;
        var advancement = manager.getAdvancement(DISCOVER_GUIDE_METEOR);
        if(advancement == null) return;
        if(advancement.isGranted(p)) return;

        lastDistanceSquared = p.getLocation().distanceSquared(fallenMeteorLocation);
        if(lastDistanceSquared > (12D*12D)) return;

        AdvancementHolder holder = holder(p);
        if(holder!= null) {
            holder.getAdvancementTracker().apply(GuideDiscoverMeteorObjective.class, (man, ad, objective) -> {
                objective.trigger(p.getUniqueId(), man, ad, LootContext.builder().looter(p).location(fallenMeteorLocation).build());
            });
        }

        if(advancement.isGranted(p)) Lang.GUIDE_STRUCTURE_NEARBY.use(p);
    }

    private AdvancementHolder holder(@NotNull Entity p){
        return EntityMemory.getDataHolder(p, AdvancementHolder.class);
    }

    protected final double maxParticleShowDistance = 16D*16D;
    public void tickStructureGuide(@NotNull Player p){
        if(fallenMeteorLocation == null) return;
        if(lastDistanceSquared < maxParticleShowDistance) return;
        //Entity guardian = death.getGuardian();
        Location pos =  fallenMeteorLocation;

        var cfg = USurvivePlugin.inst().values();
        //Location startingPoint = CruxLoc.shiftToward(p.getEyeLocation(), pos, 1D);
        new DeathGuide(p, p.getLocation().add(0, p.getHeight()/2, 0), pos,
            cfg.DEATH_GUIDE_PARTICLE_AMOUNT().value().intValue(),
            cfg.DEATH_GUIDE_PARTICLE_SHIFT_START(),
            cfg.DEATH_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER(),
            cfg.DEATH_GUIDE_PARTICLE_OFFSET_VERTICAL(),
            cfg.DEATH_GUIDE_PARTICLE_OFFSET_SIDE(),
            cfg.DEATH_GUIDE_PARTICLE_SHIFT(),
            Particle.FLAME
        ).setStarted();
    }
}
