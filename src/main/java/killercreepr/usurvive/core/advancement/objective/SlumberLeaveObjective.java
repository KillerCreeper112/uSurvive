package killercreepr.usurvive.core.advancement.objective;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxadvancements.api.advancement.ObjectiveAdvancement;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.core.advancement.objective.NumberObjective;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import killercreepr.usurvive.api.event.EntityLeaveSlumberEvent;
import killercreepr.usurvive.api.event.PostItemInfusePotionsEvent;
import killercreepr.usurvive.core.loot.USurviveEventLootContexts;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SlumberLeaveObjective extends GenericEventObjective<EntityLeaveSlumberEvent> {
    public SlumberLeaveObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(EntityLeaveSlumberEvent event) {
        return USurviveEventLootContexts.builder(event).build();
    }
}
