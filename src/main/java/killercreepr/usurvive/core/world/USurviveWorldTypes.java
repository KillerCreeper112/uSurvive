package killercreepr.usurvive.core.world;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.USurvivePlugin;
import net.kyori.adventure.key.Key;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class USurviveWorldTypes {
    public static final CruxWorldType RESOURCE_OVERWORLD = new CruxWorldType() {
        @Override
        public @NotNull CruxWorld generate(@NotNull Key name) {
            World world = new WorldCreator(name.value()).createWorld();
            if(world==null) throw new IllegalStateException("Cannot generate world! " + key());
            world.setDifficulty(Difficulty.HARD);
            CruxTag.set(world, "last_reset", PersistentDataType.LONG, System.currentTimeMillis());

            ValuesProvider cfg = USurvivePlugin.inst().values();

            int borderSize = cfg.RESOURCE_OVERWORLD_BORDER().value().intValue();
            if(borderSize > 0){
                world.getWorldBorder().setCenter(0, 0);
                world.getWorldBorder().setSize(borderSize);
            }
            CruxWorldManager worldManager = CruxCore.inst().worldManager();
            CruxWorld crux = worldManager.getWorld(name);
            Objects.requireNonNull(crux, "Unable to generate world " + name + "!");
            return crux;
        }

        @Override
        public @NotNull Key defaultWorldKey() {
            return Key.key("world_resource_overworld");
        }

        @Override
        public @NotNull Key key() {
            return Crux.key("resource_overworld");
        }
    };
    public static final CruxWorldType RESOURCE_NETHER = new CruxWorldType() {
        @Override
        public @NotNull CruxWorld generate(@NotNull Key name) {
            World world = new WorldCreator(name.value()).environment(World.Environment.NETHER).createWorld();
            if(world==null) throw new IllegalStateException("Cannot generate world! " + key());
            world.setDifficulty(Difficulty.HARD);

            ValuesProvider cfg = USurvivePlugin.inst().values();

            int borderSize = cfg.RESOURCE_NETHER_BORDER().value().intValue();
            if(borderSize > 0){
                world.getWorldBorder().setCenter(0, 0);
                world.getWorldBorder().setSize(borderSize);
            }
            CruxWorldManager worldManager = CruxCore.inst().worldManager();
            CruxWorld crux = worldManager.getWorld(name);
            Objects.requireNonNull(crux, "Unable to generate world " + name + "!");
            return crux;
        }

        @Override
        public @NotNull Key defaultWorldKey() {
            return Key.key("world_resource_nether");
        }

        @Override
        public @NotNull Key key() {
            return Crux.key("resource_nether");
        }
    };
}
