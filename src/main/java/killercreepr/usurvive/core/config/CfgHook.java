package killercreepr.usurvive.core.config;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.entity.predicate.EntityPredicate;
import killercreepr.crux.api.loot.conditions.LootCondition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.loot.conditions.entity.EntityCondition;
import killercreepr.cruxadvancements.api.advancement.objective.AdvancementObjective;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.config.CruxAdvanceCfgData;
import killercreepr.cruxadvancements.core.config.handler.FileAdvancementObjective;
import killercreepr.cruxadvancements.core.config.handler.FileSimpleAdvanceObjective;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.bukkit.handler.impl.loot.FileLootCondition;
import killercreepr.cruxconfig.config.bukkit.handler.impl.loot.SimpleFileLootCondition;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.advancement.objective.*;
import killercreepr.usurvive.core.config.handler.FileGuideProgression;
import killercreepr.usurvive.core.loot.condition.BaseItemUpgradedFromCondition;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CfgHook {
    public static final FileGuideProgression FILE_GUIDE_PROGRESSION = new FileGuideProgression();
    private static final FileAdvancementObjective fileAdvancementObjective = CruxAdvanceCfgData.fileAdvancementObjective();
    public static void load() {
        registerLoot(BukkitCfgHandlers.LOOT_CONDITION);

        fileAdvancementObjective.registerCustomHandler(new FileSimpleAdvanceObjective<>(USurvivePlugin.inst().key("item_infuse_potion")) {

            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if (maxProgress == null) maxProgress = 1;
                return new ItemInfusePotionObjective(data, maxProgress);
            }
        });
        fileAdvancementObjective.registerCustomHandler(new FileSimpleAdvanceObjective<>(USurvivePlugin.inst().key("guide_shard_choose")) {

            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if (maxProgress == null) maxProgress = 1;
                return new GuideShardChooseObjective(data, maxProgress);
            }
        });
        fileAdvancementObjective.registerCustomHandler(new FileSimpleAdvanceObjective<>(USurvivePlugin.inst().key("guide_discover_meteor")) {

            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if (maxProgress == null) maxProgress = 1;
                return new GuideDiscoverMeteorObjective(data, maxProgress);
            }
        });
        fileAdvancementObjective.registerCustomHandler(new FileSimpleAdvanceObjective<>(USurvivePlugin.inst().key("slumber")) {

            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if (maxProgress == null) maxProgress = 1;
                return new SlumberObjective(data, maxProgress);
            }
        });
        fileAdvancementObjective.registerCustomHandler(new FileSimpleAdvanceObjective<>(USurvivePlugin.inst().key("slumber_deep")) {

            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if (maxProgress == null) maxProgress = 1;
                return new SlumberDeepObjective(data, maxProgress);
            }
        });
        fileAdvancementObjective.registerCustomHandler(new FileSimpleAdvanceObjective<>(USurvivePlugin.inst().key("slumber_leave")) {

            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if (maxProgress == null) maxProgress = 1;
                return new SlumberLeaveObjective(data, maxProgress);
            }
        });
        fileAdvancementObjective.registerCustomHandler(new FileSimpleAdvanceObjective<>(USurvivePlugin.inst().key("reflect_projectile")) {

            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if (maxProgress == null) maxProgress = 1;
                return new ReflectProjectileObjective(data, maxProgress);
            }
        });
    }

    public static void registerLoot(@NotNull FileLootCondition file){

        file.registerCustomHandler(new SimpleFileLootCondition<>(USurvivePlugin.inst().key("base_item_upgraded_from")) {
            @Override
            public @NotNull LootCondition deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull String target) {
                FileRegistry registry = ctx.getRegistry();
                return new BaseItemUpgradedFromCondition(
                    target, e.getOrDefaultObject("type", null)
                );
            }
        });
    }

}
