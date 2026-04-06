package killercreepr.usurvive.core.guide;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxstructures.api.structure.Structure;
import killercreepr.cruxstructures.api.structure.generation.StructureGenerator;
import killercreepr.cruxstructures.api.structure.generation.result.GenerateResult;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<Location> placeStructure() {
        return placeStructureAttempt(0);
    }

    private CompletableFuture<Location> placeStructureAttempt(int attempt) {
        if (attempt >= maxAttempts) {
            return CompletableFuture.completedFuture(null);
        }

        if (!stillValid.value()) {
            return CompletableFuture.completedFuture(null);
        }

        int x = maxChunkDistance.value().intValue();
        int z = maxChunkDistance.value().intValue();
        if (CruxMath.random().nextBoolean()) x *= -1;
        if (CruxMath.random().nextBoolean()) z *= -1;

        int chunkX = center.getX() + x;
        int chunkZ = center.getZ() + z;
        long key = Chunk.getChunkKey(chunkX, chunkZ);

        if (alreadyChecked.contains(key)) {
            return placeStructureAttempt(attempt + 1);
        }

        alreadyChecked.add(key);

        return world.getChunkAtAsync(chunkX, chunkZ)
          .thenCompose(chunk -> {
              if (!stillValid.value()) {
                  return CompletableFuture.completedFuture(null);
              }
              return attemptPlace(chunk, gen);
          })
          .handle((location, throwable) -> {
              if (throwable != null) {
                  throwable.printStackTrace();
                  return null;
              }
              return location;
          })
          .thenCompose(location -> {
              if (location != null) {
                  return CompletableFuture.completedFuture(location);
              }
              return placeStructureAttempt(attempt + 1);
          });
    }

    public CompletableFuture<GenerateResult> attemptPlaceResult(Chunk chunk, StructureGenerator gen) {
        Structure structure = gen.generateStructure(chunk);
        if (structure == null) {
            return CompletableFuture.completedFuture(null);
        }

        return gen.generate(structure, chunk)
          .thenApply(result -> {
              if (result == null) return null;

              var event = result.getPlaceEvent();
              return (event == null || event.isCancelled()) ? null : result;
          });
    }

    public CompletableFuture<Location> attemptPlace(Chunk chunk, StructureGenerator gen) {
        Structure structure = gen.generateStructure(chunk);
        if (structure == null) {
            return CompletableFuture.completedFuture(null);
        }

        return gen.generate(structure, chunk)
          .thenApply(result -> {
              if (result == null) return null;

              var event = result.getPlaceEvent();
              return (event == null || event.isCancelled()) ? null : event.getLocation();
          });
    }

    /*public CompletableFuture<Location> placeStructure(){
        return CompletableFuture.supplyAsync(() ->{
            for(int i = 0; i < maxAttempts; i++){
                int x = maxChunkDistance.value().intValue();
                int z = maxChunkDistance.value().intValue();
                if(CruxMath.random().nextBoolean()) x *= -1;
                if(CruxMath.random().nextBoolean()) z *= -1;
                long key = Chunk.getChunkKey(center.getX()+x, center.getZ()+z);
                if(alreadyChecked.contains(key)) continue;

                Chunk chunk;
                try {
                    chunk = world.getChunkAtAsync(
                        center.getX()+x, center.getZ()+z
                    ).get(8L, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    continue;
                }
                if(!stillValid.value()) return null;
                alreadyChecked.add(key);
                Location placed = attemptPlace(chunk, gen);
                if(placed!= null) return placed;
            }
            return null;
        });
    }

    public Location attemptPlace(Chunk chunk, StructureGenerator gen){
        var structure = gen.generateStructure(chunk);
        if(structure == null) return null;
        var result = gen.generate(structure, chunk);
        result.whenComplete()
        var event = result.getPlaceEvent();
        return event == null || event.isCancelled() ? null : event.getLocation();
    }*/
}
