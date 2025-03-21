package killercreepr.usurvive.core.component.teleport;

import killercreepr.crux.api.key.tag.KeyPredicate;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.cruxteleport.api.component.TeleportLocationCheck;
import killercreepr.cruxworlds.api.world.CruxWorld;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class TeleportCheckInStructure implements TeleportLocationCheck {
    protected final KeyPredicate blacklist;

    public TeleportCheckInStructure(KeyPredicate blacklist) {
        this.blacklist = blacklist;
    }

    public KeyPredicate getBlacklist() {
        return blacklist;
    }

    @Override
    public boolean test(@NotNull Location location) {
        CruxWorld world = CruxCore.core().worldManager().getWorld(location.getWorld().key());
        if(world == null) return true;
        var module = world.getModule(StructureWorldModule.class);
        if(module == null) return true;
        Vector vec = location.toVector();
        StoredStructure stored = module.getFirstStoredAt(
            StoredStructure.class, location.getBlock(), check ->{
                BoundingBox box = check.getOrDefault(StoredStructureComponents.OUTER_BOX, check.getBoundingBox());
                return box.contains(vec) && blacklist.test(check.getParent().key());
            }
        );
        return stored == null;
    }
}
