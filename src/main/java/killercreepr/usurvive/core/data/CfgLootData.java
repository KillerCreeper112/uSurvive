package killercreepr.usurvive.core.data;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.paper.ItemHolder;

public record CfgLootData(float chance, ItemHolder item, NumberProvider amount) {
}
