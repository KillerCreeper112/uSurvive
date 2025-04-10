package killercreepr.usurvive.core.advancement.objective;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import killercreepr.usurvive.api.event.EntityDeepSlumberEvent;
import killercreepr.usurvive.core.loot.USurviveEventLootContexts;
import org.jetbrains.annotations.NotNull;

public class SlumberDeepObjective extends GenericEventObjective<EntityDeepSlumberEvent> {
    public SlumberDeepObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(EntityDeepSlumberEvent event) {
        return USurviveEventLootContexts.builder(event).build();
    }
}
