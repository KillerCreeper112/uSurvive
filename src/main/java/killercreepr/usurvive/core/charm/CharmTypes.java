package killercreepr.usurvive.core.charm;

import killercreepr.crux.core.Crux;
import killercreepr.cruxcharms.api.charm.CharmParser;
import killercreepr.cruxcharms.core.registries.CharmRegistries;

public class CharmTypes {
    public static void register(){}
    public static final CharmParser MINE_BONUS = register(CharmParser.charmParser(Crux.key("mine_bonus"), CharmCompParsers.MINE_BONUS));
    private static CharmParser register(CharmParser parser){
        return CharmRegistries.CHARM_PARSER.register(parser);
    }
}
