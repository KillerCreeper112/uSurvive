package killercreepr.usurvive.core.config;

import killercreepr.crux.api.loot.conditions.LootCondition;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
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
import killercreepr.usurvive.core.config.handler.*;
import killercreepr.usurvive.core.loot.condition.BaseItemUpgradedFromCondition;
import killercreepr.usurvive.core.loot.condition.EntityHasRankCondition;
import killercreepr.usurvive.core.loot.condition.PlayerHasItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CfgHook {
    public static final FileGuideProgression FILE_GUIDE_PROGRESSION = new FileGuideProgression();
    public static final FileStoryProgression FILE_STORY_PROGRESSION = new FileStoryProgression();
    public static final FileStoryProgressData FILE_STORY_PROGRESS_DATA = new FileStoryProgressData(FILE_STORY_PROGRESSION);
    public static final FileTipCategory TIP_CATEGORY = new FileTipCategory();
    private static final FileAdvancementObjective fileAdvancementObjective = CruxAdvanceCfgData.fileAdvancementObjective();
    public static final FileRank FILE_RANK = new FileRank();

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
        file.registerCustomHandler(new SimpleFileLootCondition<>(USurvivePlugin.inst().key("has_rank")) {
            @Override
            public @NotNull LootCondition deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull String target) {
                FileRegistry registry = ctx.getRegistry();
                return new EntityHasRankCondition(
                    target, registry.deserializeFromFile(NumberProvider.class, e.get("rank"))
                );
            }
        });
        file.registerCustomHandler(new SimpleFileLootCondition<>(USurvivePlugin.inst().key("player_has_item")) {
            @Override
            public @NotNull LootCondition deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull String target) {
                FileRegistry registry = ctx.getRegistry();
                return new PlayerHasItemCondition(
                    target,
                    registry.deserializeFromFile(LootCondition.class, e.get("item_condition")),
                    registry.deserializeFromFileOrDefault(Integer.class, e.get("amount"), 1)
                );
            }
        });
    }

}
