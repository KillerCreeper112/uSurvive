package killercreepr.usurvive.core.guide.listener;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.cruxadvancements.api.advancement.CruxAdvancement;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.api.event.CruxAdvancementGrantEvent;
import killercreepr.cruxblocks.api.event.CruxBlockBreakEvent;
import killercreepr.cruxblocks.core.mining.user.EntityMiner;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.structure.generation.StructureGenerator;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.guide.GuideStartHolder;
import killercreepr.usurvive.core.guide.GuideStructurePlacer;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.world.USurviveWorldTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class GuideListener implements Listener {
    public static final Key ENTER_RESOURCE_OVERWORLD = USurvivePlugin.inst().key("main/enter_resource_world");

    protected final Holder<StructureGenerator> GUIDE_RESOURCE_WORLD_STRUCTURE;
    protected final Holder<NumberProvider> GUIDE_RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE;

    public GuideListener(Holder<StructureGenerator> GUIDE_RESOURCE_WORLD_STRUCTURE, Holder<NumberProvider> guideResourceWorldStructureChunkDistance) {
        this.GUIDE_RESOURCE_WORLD_STRUCTURE = GUIDE_RESOURCE_WORLD_STRUCTURE;
        GUIDE_RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE = guideResourceWorldStructureChunkDistance;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCruxBlockBreak(CruxBlockBreakEvent event) {
        if(!(event.getContext().getMiner() instanceof EntityMiner miner)) return;
        if(!(miner.getEntity() instanceof Player p)) return;
        if(!event.getBlock().key().equals(Crux.key("orbit_ore"))) return;

        GuideStartHolder holder = EntityMemory.getDataHolder(p, GuideStartHolder.class);
        if(holder == null) return;
        holder.setFallenMeteorLocation(null);
        holder.deleteSaveFile();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCruxAdvancementGrant(CruxAdvancementGrantEvent event) {
        CruxAdvancementManager manager = event.getManager();
        CruxAdvancement advancement = event.getAdvancement();
        if(!advancement.key().equals(ENTER_RESOURCE_OVERWORLD)) return;
        UUID who = event.getWho();
        Player p = Crux.getServer().getPlayer(who);
        if(p == null) return;

        World world = p.getWorld();
        CruxWorld cruxWorld = CruxCore.core().worldManager().getWorld(world.key());
        if(cruxWorld==null) return;
        CruxWorldType type = cruxWorld.get(CruxWorldsComponents.WORLD_TYPE);
        if(!USurviveWorldTypes.RESOURCE_OVERWORLD.compare(type)) return;

        StructureGenerator gen = GUIDE_RESOURCE_WORLD_STRUCTURE.value();
        if(gen == null) return;

        GuideStructurePlacer placer = new GuideStructurePlacer(p.getChunk(), 48, gen, GUIDE_RESOURCE_WORLD_STRUCTURE_CHUNK_DISTANCE.value(),
            p::isOnline);
        placer.placeStructure().whenComplete((loc, throwable) ->{
            if(throwable != null) throwable.printStackTrace();

            if(!p.isOnline()){
                if(loc != null) Crux.logError(p.getName() + " guide structure was placed even though they are offline!");
                return;
            }
            if(loc == null){
                manager.revokeAdvancement(who, event.getAdvancement());
                Lang.GUIDE_STRUCTURE_CANNOT_BE_PLACED.use(p);
                return;
            }

            GuideStartHolder holder = EntityMemory.getOrCreateDataHolder(p, GuideStartHolder.class, mem -> new GuideStartHolder((PlayerMemory) mem));
            if(holder == null){
                p.sendActionBar(Component.text("An error occurred. Contact developer. CODE 7391"));
                return;
            }
            holder.setFallenMeteorLocation(loc);
            Lang.GUIDE_STRUCTURE_PLACED.use(p);
        });
    }
}
