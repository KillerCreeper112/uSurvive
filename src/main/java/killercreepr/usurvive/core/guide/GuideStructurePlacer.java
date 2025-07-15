package killercreepr.usurvive.core.guide;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxstructures.api.structure.generation.StructureGenerator;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GuideStructurePlacer {
    protected final World world;
    protected final Chunk center;
    protected final int maxAttempts;
    protected final StructureGenerator gen;
    protected final NumberProvider maxChunkDistance;
    protected final Collection<Long> alreadyChecked = new HashSet<>();
    protected final Holder<Boolean> stillValid;

    public GuideStructurePlacer(Chunk center, int maxAttempts, StructureGenerator gen, NumberProvider maxChunkDistance, Holder<Boolean> stillValid) {
        this.world = center.getWorld();
        this.center = center;
        this.maxAttempts = maxAttempts;
        this.gen = gen;
        this.maxChunkDistance = maxChunkDistance;
        this.stillValid = stillValid;
    }

    public CompletableFuture<Location> placeStructure(){
        return CompletableFuture.supplyAsync(() ->{
            for(int i = 0; i < maxAttempts; i++){
                int x = maxChunkDistance.value().intValue();
                int z = maxChunkDistance.value().intValue();
                Chunk chunk;
                try {
                    chunk = world.getChunkAtAsync(
                        center.getX()+x, center.getZ()+z
                    ).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                if(!stillValid.value()) return null;
                if(alreadyChecked.contains(chunk.getChunkKey())) continue;
                alreadyChecked.add(chunk.getChunkKey());
                Location placed = attemptPlace(chunk, gen);
                if(placed!= null) return placed;
            }
            return null;
        });
    }

    public Location attemptPlace(Chunk chunk, StructureGenerator gen){
        var result = gen.generate(chunk);
        var event = result.getPlaceEvent();
        return event == null || event.isCancelled() ? null : event.getLocation();
    }
}
