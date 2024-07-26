package killercreepr.usurvive.world;

import killercreepr.usurvive.persistence.USurvivePersist;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class WorldUtil {
    public static @NotNull String getDimensionID(@NotNull World world){
        String custom = USurvivePersist.DIMENSION.get(world, null);
        if(custom != null) return custom;
        World.Environment environment = world.getEnvironment();
        if(environment == World.Environment.NORMAL) return "overworld";
        if(environment == World.Environment.NETHER) return "nether";
        if(environment == World.Environment.THE_END) return "the_end";
        return "custom";
    }
}
