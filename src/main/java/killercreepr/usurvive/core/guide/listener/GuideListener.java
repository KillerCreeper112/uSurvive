package killercreepr.usurvive.core.guide.listener;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxadvancements.api.advancement.CruxAdvancement;
import killercreepr.cruxadvancements.api.event.CruxAdvancementGrantEvent;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.structure.generation.StructureGenerator;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.guide.GuideStartHolder;
import killercreepr.usurvive.core.guide.GuideStructurePlacer;
import killercreepr.usurvive.core.world.USurviveWorldTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GuideListener implements Listener {
    public static final Key ENTER_RESOURCE_OVERWORLD = USurvivePlugin.inst().key("main/enter_resource_world");

    protected final Holder<StructureGenerator> GUIDE_RESOURCE_WORLD_STRUCTURE;
    protected final Holder<NumberProvider> GUIDE_RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE;

    public GuideListener(Holder<StructureGenerator> GUIDE_RESOURCE_WORLD_STRUCTURE, Holder<NumberProvider> guideResourceWorldStructureChunkDistance) {
        this.GUIDE_RESOURCE_WORLD_STRUCTURE = GUIDE_RESOURCE_WORLD_STRUCTURE;
        GUIDE_RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE = guideResourceWorldStructureChunkDistance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCruxAdvancementGrant(CruxAdvancementGrantEvent event) {
        CruxAdvancement advancement = event.getAdvancement();
        if(!advancement.key().equals(ENTER_RESOURCE_OVERWORLD)) return;
        Player p = Crux.getServer().getPlayer(event.getWho());
        if(p == null) return;

        World world = p.getWorld();
        CruxWorld cruxWorld = CruxCore.core().worldManager().getWorld(world.key());
        if(cruxWorld==null) return;
        CruxWorldType type = cruxWorld.get(CruxWorldsComponents.WORLD_TYPE);
        if(!USurviveWorldTypes.RESOURCE_OVERWORLD.compare(type)) return;

        StructureGenerator gen = GUIDE_RESOURCE_WORLD_STRUCTURE.value();
        if(gen == null) return;

        GuideStructurePlacer placer = new GuideStructurePlacer(p.getChunk(), 32, gen, GUIDE_RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE.value(),
            p::isOnline);
        placer.placeStructure().whenComplete((loc, throwable) ->{
            if(throwable != null) throwable.printStackTrace();

            if(!p.isOnline()){
                if(loc != null) Crux.logError(p.getName() + " guide structure was placed even though they are offline!");
                return;
            }

            GuideStartHolder holder = EntityMemory.getOrCreateDataHolder(p, GuideStartHolder.class, mem -> new GuideStartHolder((PlayerMemory) mem));
            if(holder == null) return;
            holder.setFallenMeteorLocation(loc);
        });
    }
}
