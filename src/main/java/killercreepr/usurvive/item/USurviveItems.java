package killercreepr.usurvive.item;

import killercreepr.crux.Crux;
import killercreepr.crux.tags.container.MergedTagContainer;
import killercreepr.cruxblocks.persistence.CruxBlocksPersistTags;
import killercreepr.cruxitems.item.CruxedItem;
import killercreepr.cruxitems.item.plugin.GenericPluginItem;
import killercreepr.cruxitems.item.plugin.PluginItem;
import killercreepr.cruxitems.registries.CruxItemRegistries;
import killercreepr.usurvive.block.USurviveBlocks;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class USurviveItems {
    public static void register(){};
    public static final PluginItem CHARRED_LOG = CruxItemRegistries.ITEMS.register(new GenericPluginItem(Crux.key("charred_log")) {
        @Override
        public @NotNull CruxedItem build(@Nullable Entity e, @Nullable MergedTagContainer tags) {
            CruxedItem item = new CruxedItem(Material.PAPER);
            item.edit(i -> CruxBlocksPersistTags.CRUX_BLOCK_GROUP.set(i, USurviveBlocks.CHARRED_LOG));
            return item;
        }
    });
}
