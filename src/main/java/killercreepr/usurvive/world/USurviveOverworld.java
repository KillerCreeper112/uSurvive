package killercreepr.usurvive.world;

import killercreepr.cruxworlds.world.NaturalEntitySpawnManager;
import killercreepr.cruxworlds.world.SimpleWorld;
import killercreepr.cruxworlds.world.entity.entity.NaturalEntitySpawner;
import killercreepr.cruxworlds.world.entity.entity.impl.SimpleNaturalEntitySpawner;
import killercreepr.usurvive.USurvivePlugin;
import killercreepr.usurvive.registries.USurviveRegistries;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class USurviveOverworld extends SimpleWorld {
    protected final NaturalEntitySpawnManager entitySpawnManager;
    public USurviveOverworld(@NotNull World world) {
        this(world, new Random(world.getSeed()));
    }

    public USurviveOverworld(@NotNull World world, @NotNull Random random) {
        super(world, random);
        entitySpawnManager = new NaturalEntitySpawnManager(this, createEntitySpawner());
    }

    public NaturalEntitySpawner createEntitySpawner(){
        return new SimpleNaturalEntitySpawner(USurvivePlugin.inst(), random,
            USurviveRegistries.OVERWORLD_NATURAL_ENTITY_SPAWN_GROUP,
            200, 34, 10);
    }


    protected boolean active = false;

    public BukkitRunnable buildRunnable(){
        return new BukkitRunnable() {
            @Override
            public void run() {
                if(!isActive()){
                    cancel();
                    return;
                }
                tick();
            }
        };
    }

    public boolean isActive(){
        return active;
    }

    public void tick(){

    }

    @Override
    public void onLoad() {
        active = true;
        buildRunnable().runTaskTimer(USurvivePlugin.inst(), tickInterval(), tickInterval());
    }

    public int tickInterval(){
        return 100;
    }

    @Override
    public void onUnload() {
        active = false;
    }
}
