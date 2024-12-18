package killercreepr.usurvive.core.item;

import killercreepr.crux.core.Crux;
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.cruxitems.core.registries.CruxItemRegistries;

public class USurviveItems {
    public static void register(){};
    public static PluginItem GRAPPLE = CruxItemRegistries.ITEMS.get(Crux.key("grapple"));
    public static PluginItem QUICK_ACCESS = CruxItemRegistries.ITEMS.get(Crux.key("quick_access"));
}
