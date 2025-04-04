package killercreepr.usurvive.core.advancement.objective;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxadvancements.api.advancement.ObjectiveAdvancement;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.core.advancement.objective.NumberObjective;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.usurvive.api.event.PostItemInfusePotionsEvent;
import killercreepr.usurvive.core.loot.USurviveEventLootContexts;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemInfusePotionObjective extends NumberObjective {
    public ItemInfusePotionObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    public boolean trigger(@NotNull UUID who,
                           @NotNull CruxAdvancementManager manager,
                           @NotNull ObjectiveAdvancement advancement,
                           @NotNull PostItemInfusePotionsEvent event){
        LootContext ctx = USurviveEventLootContexts.builder(event).build();
        return trigger(
            who, manager, advancement, ctx
        );
    }
}
