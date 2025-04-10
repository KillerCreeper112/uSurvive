package killercreepr.usurvive.core.advancement.objective;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import killercreepr.usurvive.api.event.EntitySlumberEvent;
import killercreepr.usurvive.core.loot.USurviveEventLootContexts;
import org.jetbrains.annotations.NotNull;

public class SlumberObjective extends GenericEventObjective<EntitySlumberEvent> {
    public SlumberObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(EntitySlumberEvent event) {
        return USurviveEventLootContexts.builder(event).build();
    }
}
