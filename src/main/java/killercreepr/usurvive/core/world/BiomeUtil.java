package killercreepr.usurvive.core.world;

import killercreepr.crux.core.Crux;
import net.kyori.adventure.key.Key;

public class BiomeUtil {
    /*public static @NotNull String getDimensionID(@NotNull World world){
        String custom = USurvivePersist.DIMENSION.get(world, null);
        if(custom != null) return custom;
        World.Environment environment = world.getEnvironment();
        if(environment == World.Environment.NORMAL) return "overworld";
        if(environment == World.Environment.NETHER) return "nether";
        if(environment == World.Environment.THE_END) return "the_end";
        return "custom";
    }*/

    public static final Key ELDRITCH_WASTES = Crux.key("eldritch_wastes");
    public static boolean isEldritchWastes(Key key){
        return key.equals(ELDRITCH_WASTES);
    }
}
