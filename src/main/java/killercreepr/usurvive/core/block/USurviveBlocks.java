package killercreepr.usurvive.core.block;

import killercreepr.crux.core.Crux;
import killercreepr.cruxblocks.api.block.group.CruxBlockGroup;
import killercreepr.cruxblocks.core.registries.CruxBlocksRegistries;

public class USurviveBlocks {
    public static void register(){}
    public static final CruxBlockGroup ORBIT_ORE = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("orbit_ore"));
    public static final CruxBlockGroup CHARRED_LOG = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("charred_log"));
    public static final CruxBlockGroup EMBER_LOG = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("ember_log"));
    public static final CruxBlockGroup SPECTRAL_SOIL = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("spectral_soil"));
}
