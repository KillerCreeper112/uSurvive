package killercreepr.usurvive.world;

import killercreepr.crux.core.Crux;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
import killercreepr.usurvive.USurvivePlugin;
import killercreepr.usurvive.values.ValuesProvider;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class USurviveWorldTypes {
    public static final CruxWorldType RESOURCE_OVERWORLD = new CruxWorldType() {
        @Override
        public @NotNull CruxWorld generate(@NotNull String name) {
            World world = new WorldCreator(name).createWorld();
            if(world==null) throw new IllegalStateException("Cannot generate world! " + key());

            ValuesProvider cfg = USurvivePlugin.inst().values();

            int borderSize = cfg.RESOURCE_OVERWORLD_BORDER().value().intValue();
            if(borderSize > 0){
                world.getWorldBorder().setCenter(0, 0);
                world.getWorldBorder().setSize(borderSize);
            }
            CruxWorldManager worldManager = CruxCore.inst().worldManager();
            return Objects.requireNonNull(worldManager.getWorld(name), "Unable to generate world " + name + "!");
        }

        @Override
        public @NotNull String defaultWorldName() {
            return "world_resource_overworld";
        }

        @Override
        public @NotNull Key key() {
            return Crux.key("resource_overworld");
        }
    };
}
