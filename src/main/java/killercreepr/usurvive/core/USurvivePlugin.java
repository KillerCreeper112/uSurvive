package killercreepr.usurvive.core;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.communication.Communicator;
import killercreepr.crux.api.communication.lang.CreateLang;
import killercreepr.crux.api.communication.lang.LangProvider;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.api.registry.Registry;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.communication.MsgContainer;
import killercreepr.crux.core.communication.lang.LangPopulator;
import killercreepr.crux.core.communication.lang.Msg;
import killercreepr.crux.core.communication.lang.SimpleCreateLang;
import killercreepr.crux.core.data.util.Pair;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.plugin.module.StandardModules;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxString;
import killercreepr.cruxadvancements.core.config.loader.CfgCrazyAdvancementManagerCfgLoader;
import killercreepr.cruxadvancements.core.config.loader.GlobalAdvancementManagerCfgLoader;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.bukkit.loader.CfgLoader;
import killercreepr.cruxconfig.config.bukkit.standard.SimpleLangConfig;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.AutoFileOptions;
import killercreepr.cruxconfig.config.common.handler.FileConvertHandler;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxcore.recipes.BrewingRecipeLoader;
import killercreepr.cruxenchants.api.enchant.CruxEnchant;
import killercreepr.cruxenchants.core.registries.CruxEnchantRegistries;
import killercreepr.cruxenchantsoverhaul.api.enchant.EEnchant;
import killercreepr.cruxenchantsoverhaul.registries.EnchantsRegistries;
import killercreepr.cruxfriends.api.friend.FriendsHolder;
import killercreepr.cruxfriends.core.CruxFriendsPlugin;
import killercreepr.cruxitems.api.item.CruxedItem;
import killercreepr.cruxitems.api.item.CruxedItemUpdater;
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.cruxitems.core.config.CruxItemsConfigHook;
import killercreepr.cruxitems.core.config.handler.FilePluginItemHandler;
import killercreepr.cruxitems.core.item.CruxedItemUpdateContext;
import killercreepr.cruxitems.core.registries.CruxItemRegistries;
import killercreepr.cruxmenus.CruxMenusModule;
import killercreepr.cruxmenus.api.menu.CfgMenu;
import killercreepr.cruxmenus.api.menu.action.MenuAction;
import killercreepr.cruxmenus.api.menu.config.handler.FileMenuHolder;
import killercreepr.cruxmenus.api.menu.holder.MenuItems;
import killercreepr.cruxmenus.api.menu.module.MenuModule;
import killercreepr.cruxmenus.api.menu.module.config.MenuModuleBuilder;
import killercreepr.cruxmenus.api.menu.registry.MenuRegistry;
import killercreepr.cruxmenus.core.menu.holder.SimpleMenuHolder;
import killercreepr.cruxmenus.core.menu.module.standard.SimpleFilePagedCfg;
import killercreepr.cruxmenus.core.menu.module.standard.SimplePagedMenuModule;
import killercreepr.cruxparty.api.party.Party;
import killercreepr.cruxparty.api.party.PartyHolder;
import killercreepr.cruxparty.core.CruxPartyPlugin;
import killercreepr.cruxpotions.core.config.CruxPotionCfgHandler;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.creator.CruxWorldModuleCreator;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.api.world.module.WorldModule;
import killercreepr.cruxworlds.core.registries.WorldsRegistries;
import killercreepr.usurvive.api.death.DeathManager;
import killercreepr.usurvive.api.dialog.*;
import killercreepr.usurvive.api.entity.player.UPlayerManager;
import killercreepr.usurvive.api.entity.player.data.RandomTPData;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsManager;
import killercreepr.usurvive.api.guide.GuidePoint;
import killercreepr.usurvive.api.guide.tips.Tip;
import killercreepr.usurvive.api.guide.tips.TipCategory;
import killercreepr.usurvive.api.rank.Rank;
import killercreepr.usurvive.api.story.StoryProgressData;
import killercreepr.usurvive.api.story.StoryProgression;
import killercreepr.usurvive.api.story.StoryState;
import killercreepr.usurvive.api.tpa.TPAManager;
import killercreepr.usurvive.api.values.SleeperCfg;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.advancement.auto.KillMobAdvanceMaker;
import killercreepr.usurvive.core.attribute.USurviveAttribute;
import killercreepr.usurvive.core.block.USurviveBlocks;
import killercreepr.usurvive.core.block.component.USurviveBlockComponents;
import killercreepr.usurvive.core.charm.CharmTypes;
import killercreepr.usurvive.core.command.*;
import killercreepr.usurvive.core.component.DialogComponents;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.config.CfgHook;
import killercreepr.usurvive.core.config.Config;
import killercreepr.usurvive.core.config.UCfgContainer;
import killercreepr.usurvive.core.config.component.CfgUSurviveComponents;
import killercreepr.usurvive.core.config.handler.*;
import killercreepr.usurvive.core.data.CfgLootData;
import killercreepr.usurvive.core.data.PremiumRank;
import killercreepr.usurvive.core.data.ResourcePackData;
import killercreepr.usurvive.core.data.ResourcePackRequestData;
import killercreepr.usurvive.core.data.entity.DeathHolder;
import killercreepr.usurvive.core.data.entity.MemoryPlayerSettingsHolder;
import killercreepr.usurvive.core.death.PlayerDeath;
import killercreepr.usurvive.core.death.SimpleDeathManager;
import killercreepr.usurvive.core.entity.SimpleEntityHiderHolder;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.entity.memory.*;
import killercreepr.usurvive.core.entity.mob.USurviveMobCategory;
import killercreepr.usurvive.core.entity.player.SimpleUPlayerManager;
import killercreepr.usurvive.core.entity.player.settings.SimplePlayerSettingsManager;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettings;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeakness;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessCalculator;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessWarning;
import killercreepr.usurvive.core.entity.tickable.USurviveTickables;
import killercreepr.usurvive.core.guide.config.GuideCfgLoader;
import killercreepr.usurvive.core.guide.progression.GuideProgression;
import killercreepr.usurvive.core.item.QuickAccessItem;
import killercreepr.usurvive.core.item.USurviveItems;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.listener.*;
import killercreepr.usurvive.core.listener.glowapi.GlowingInitListener;
import killercreepr.usurvive.core.mechanics.grapple.standard.StandardGrappleHooks;
import killercreepr.usurvive.core.mechanics.whip.standard.StandardWhipHooks;
import killercreepr.usurvive.core.menu.ItemPackMenu;
import killercreepr.usurvive.core.menu.action.*;
import killercreepr.usurvive.core.menu.data.USurviveDataParser;
import killercreepr.usurvive.core.menu.module.AdvancementShowcaseModule;
import killercreepr.usurvive.core.menu.module.CurrencyDepositModule;
import killercreepr.usurvive.core.potion.USurvivePotions;
import killercreepr.usurvive.core.potion.inflictor.UDamageEntityInflictor;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import killercreepr.usurvive.core.stat.USurviveStats;
import killercreepr.usurvive.core.statistic.USurviveStatistics;
import killercreepr.usurvive.core.story.config.StoryCfgLoader;
import killercreepr.usurvive.core.tags.USurviveTag;
import killercreepr.usurvive.core.tags.object.*;
import killercreepr.usurvive.core.tpa.SimpleTPAManager;
import killercreepr.usurvive.core.values.DefaultValues;
import killercreepr.usurvive.core.world.USurviveNether;
import killercreepr.usurvive.core.world.USurviveOverworld;
import killercreepr.usurvive.core.world.USurviveWorldTypes;
import killercreepr.usurvive.core.world.entity.USurviveEntityGroups;
import killercreepr.usurvive.core.world.module.GenericOverworldModule;
import killercreepr.usurvive.core.world.module.OverworldMobSpawnsModule;
import killercreepr.usurvive.core.world.module.ResetWorldModule;
import killercreepr.usurvive.core.world.module.ResourceWorldModule;
import killercreepr.usurvive.external.quickshop.UQuickShop;
import net.kyori.adventure.key.Key;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

public class USurvivePlugin extends CruxPlugin implements Listener, LangProvider {

    private static USurvivePlugin instance;
    public static USurvivePlugin inst(){ return instance; }

    protected DeathManager deathManager;
    public DeathManager getDeathManager() {
        return deathManager;
    }

    protected UCfgContainer cfgContainer;

    public UCfgContainer cfgContainer() {
        return cfgContainer;
    }

    protected ValuesProvider values;

    public ValuesProvider values() {
        return values;
    }

    public void values(@NotNull ValuesProvider values) {
        this.values = values;
    }

    protected final CreateLang lang = Lang.setLang(new SimpleCreateLang());
    @NotNull
    public CreateLang lang() {
        return lang;
    }

    protected PlayerSettingsManager playerSettingsManager = new SimplePlayerSettingsManager();

    public PlayerSettingsManager getPlayerSettingsManager() {
        return playerSettingsManager;
    }

    public void setPlayerSettingsManager(PlayerSettingsManager playerSettingsManager) {
        this.playerSettingsManager = playerSettingsManager;
    }

    protected TPAManager tpaManager = new SimpleTPAManager();

    public TPAManager getTPAManager() {
        return tpaManager;
    }

    public void setTPAManager(TPAManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    protected UPlayerManager playerManager = new SimpleUPlayerManager();

    public UPlayerManager getPlayerManager() {
        return playerManager;
    }

    public void setPlayerManager(UPlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    protected final CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader = new CfgCrazyAdvancementManagerCfgLoader(this);
    protected final GlobalAdvancementManagerCfgLoader globalManagerLoader = new GlobalAdvancementManagerCfgLoader(this, "community_advancements");
    protected DevCommands devCommands;
    @Override
    public void onLoad() {
        instance = this;
        super.onLoad();
        deathManager = new SimpleDeathManager(this, CruxCore.core().worldManager(), playerSettingsManager);
        USurviveMobCategory.register();
        USurviveComponents.register();
        DialogComponents.register();
        StandardPlayerSettings.register();
        USurviveStats.register();
        USurviveStatistics.register();
        USurviveAttribute.register();
        USurviveTickables.register();
        new USurviveCommands(this).register();
        new StoreCommands(this).register();
        new TPACommands(this).register();
        devCommands = new DevCommands(this);
        devCommands.register();
        new AdminCommands(this).register();
        USurvivePotions.register();

        CfgHook.load();
        CfgRegistries.FILE.forEach(this::registerCfgHandlers);
        CfgRegistries.SIMPLE_REGISTRY.forEach(this::registerSimpleCfgHandlers);
        CfgUSurviveComponents.register(BukkitCfgHandlers.TYPED_DATA_COMPONENT.typeHandlers());

        CruxPotionCfgHandler.POTION_INFLICTOR.register(UDamageEntityInflictor.ID, new FileUDamageEntityInflictor());

        USurviveBlockComponents.register(BukkitCfgHandlers.TYPED_DATA_COMPONENT.typeHandlers());

        new GuideCfgLoader(CfgHook.FILE_GUIDE_PROGRESSION).load();
        new StoryCfgLoader(CfgHook.FILE_STORY_PROGRESSION).load();

        if(getServer().getPluginManager().getPlugin("CruxCharms") != null){
            CharmTypes.register();
        }

        CruxItemsConfigHook.filePluginItem().registerTypeHandler(new FilePluginItemHandler(){
            @Override
            public @NotNull Key key() {
                return USurvivePlugin.this.key("quick_access");
            }

            @NotNull
            @Override
            public PluginItem deserialize(@NotNull FileContext<?> ctx, @NotNull FileElement e, @NotNull Key key, @NotNull DynamicItem item) {
                return new QuickAccessItem(key, item);
            }
        });

        CruxCore.inst().cruxMenus().menuModuleRegistry().register(new MenuModuleBuilder() {
            @Override
            public @Nullable MenuModule build(@NotNull FileContext<?> ctx, @NotNull FileElement e, @Nullable FileObject menuCtx) {
                if(!(e instanceof FileObject o)) return null;
                FileRegistry reg = ctx.getRegistry();
                String id = o.getObject(String.class, "id");
                Collection<Key> acceptedCurrencies = reg.deserializeFromFile(
                    new TypeToken<Set<Key>>(){}.getType(), o.get("accepted_currencies")
                );
                NumberProvider currencyIndexes = reg.deserializeFromFile(NumberProvider.class, o.get("currency_slots"));
                Communicator depositMsg = reg.deserializeFromFile(MsgContainer.class, o.get("deposit_msg"));
                return new CurrencyDepositModule(
                    key(), id, acceptedCurrencies, currencyIndexes, depositMsg
                );
            }

            @Override
            public @NotNull Key key() {
                return Crux.key("currency_deposit");
            }
        });

        CruxCore.inst().cruxMenus().menuModuleRegistry().register(new MenuModuleBuilder() {
            @Override
            public @Nullable MenuModule build(@NotNull FileContext<?> ctx, @NotNull FileElement e, @Nullable FileObject menuCtx) {
                if(!(e instanceof FileObject o)) return null;
                FileRegistry reg = ctx.getRegistry();
                String id = o.getObject(String.class, "id");
                return new AdvancementShowcaseModule(
                    key(), id,
                    reg.deserializeFromFile(new TypeToken<List<Key>>(){}.getType(), o.get("advancement_managers")),
                    reg.deserializeFromFile(NumberProvider.class, o.get("advance_slots")),
                    reg.deserializeFromFile(NumberProvider.class, o.get("advance_manager_slots")),
                    reg.deserializeFromFile(NumberProvider.class, o.get("general_slot_offset")),
                    reg.deserializeFromFile(NumberProvider.class, o.get("center_offset_x")),
                    reg.deserializeFromFile(NumberProvider.class, o.get("center_offset_y")),
                    reg.deserializeFromFile(new TypeToken<Map<String, String>>(){}.getType(), o.get("description_color")),
                    reg.deserializeFromFile(DynamicItem.class, o.get("unknown_icon")),
                    reg.deserializeFromFile(DynamicItem.class, o.get("empty_manager_icon")),
                    reg.deserializeFromFile(NumberProvider.class, o.get("description_length")),
                    reg.deserializeFromFile(new TypeToken<List<String>>(){}.getType(), o.get("completed_lore"))
                );
            }

            @Override
            public @NotNull Key key() {
                return USurvivePlugin.this.key("advancement_showcase");
            }
        });
        CruxMenusModule menusModule = CruxCore.inst().cruxMenus();
        registerMenuModules(menusModule.menuRegistry(), menusModule.menuRegistry().menuModule(), menusModule.menuModuleRegistry());

        menusModule.menuRegistry().menuHolders().register(new SimpleMenuHolder(
            Crux.key("items_pack"), "Items Pack", NumberProvider.zero(), MenuItems.items(new TreeMap<>()),
            DataExchange.empty(), Set.of()
        ){
            @NotNull
            @Override
            public CfgMenu createMenu(@NotNull DataExchange data) {
                return new ItemPackMenu(this, data);
            }

            @NotNull
            @Override
            public CfgMenu createMenu(@NotNull DataExchange data, @Nullable MergedTagContainer tags) {
                return new ItemPackMenu(this, data, tags);
            }
        });

        Registry<MenuAction> menuActions = menusModule.menuRegistry().menuActions();
        menuActions.register(new PlayerSettingCacheAction(Crux.key("player_settings_cache")));
        menuActions.register(new PlayerSettingAction(Crux.key("player_settings")));
        menuActions.register(new ConfirmTPAAction(Crux.key("confirm_tpa")));
        menuActions.register(new CancelTPAAction(Crux.key("cancel_tpa")));
        menuActions.register(new GuideShardAction(Crux.key("guide_shard")));
        menuActions.register(new AdvancementShowcaseAction(Crux.key("advancement_showcase")));

        EntityMemory.registerFunction(this, mem ->{
            if(!(mem instanceof PlayerMemory m)) return;
            m.getDataHolders().register(new DeathHolder(m, values(), getDeathManager()));
            m.getDataHolders().register(new MemoryPlayerSettingsHolder(m));
            m.getDataHolders().register(new EntityInfusedPotionsHolder(m));
            m.getDataHolders().register(new SleeperHolder(m, () -> (SleeperCfg) values()));
            m.getDataHolders().register(new USurviveAdvancementLoaderHolder(m, cfgManagerLoader));
            m.getDataHolders().register(new AdvancementAutoSaveHolder(m, cfgManagerLoader));
            //m.getDataHolders().register(new SimpleStoryHolder(m));
            m.getDataHolders().register(new SimpleEntityHiderHolder(m));
        });
        StandardGrappleHooks.register(USurviveRegistries.GRAPPLE_HOOK);
        StandardWhipHooks.register(USurviveRegistries.WHIP_HOOK);
        USurviveEntityGroups.register(USurviveRegistries.OVERWORLD_NATURAL_ENTITY_SPAWN_GROUP);

        CruxWorldManager worldManager = CruxCore.inst().worldManager();
        registerWorldData(worldManager);
        WorldsRegistries.WORLD_TYPE.register(USurviveWorldTypes.RESOURCE_OVERWORLD);
        WorldsRegistries.WORLD_TYPE.register(USurviveWorldTypes.RESOURCE_NETHER);

        /*CruxWorldManager worldManager = CruxCore.inst().worldManager();
        worldManager.getCreatorRegistry().register("world", USurviveOverworld::new);
        worldManager.getCreatorRegistry().register("world_nether", USurviveNether::new);*/
        Crux.tags().register(List.of(
            new PlayerDeathTags(),
            new PlayerSettingsHolderTags(),
            new PlayerTags(),
            new PlayerSettingSelectionTags(),
            new PlayerSettingTags(),
            new ItemStackTags(),
            new WorldTags(),
            new SleeperHolderTags(),
            new DialogContentLinePairTags(),
            new DialogLineTags(),
            new DialogContentTags()
        ));
        if(getServer().getPluginManager().getPlugin("CruxClaims") != null){
            Crux.tags().register(new ClaimProfileTags());
        }
        Crux.format().globalStringResolvers().register(new USurviveTag());

        CruxItemRegistries.ITEM_UPDATERS.register(6, new CruxedItemUpdater() {
            @Override
            public void onUpdate(@NotNull CruxedItemUpdateContext ctx) {
                CruxedItem item = ctx.getItem();
                if(!(item.meta() instanceof EnchantmentStorageMeta meta)) return;
                if(meta.getStoredEnchants().size() != 1) return;
                String description;
                Key key = CruxCollection.getFirst(meta.getStoredEnchants().keySet()).key();
                CruxEnchant enchant = CruxEnchantRegistries.CRUX_ENCHANT.get(key);
                if(enchant == null){
                    EEnchant eEnchant = EnchantsRegistries.EENCHANT.get(key);
                    if(eEnchant == null){
                        return;
                    }
                    description = eEnchant.description();
                }else{
                    description = enchant.description();
                }
                if(description == null) return;
                item.addLoreFromString(
                    CruxString.buildDescription(description, CruxString.DEFAULT_DESCRIPTION_MAX_LENGTH,
                        s -> "<gray>" + s)
                );
            }

            @NotNull
            @Override
            public Key key() {
                return USurvivePlugin.this.key("enchanted_books");
            }
        });
        USurviveMobs.register();
    }

    public void registerWorldData(CruxWorldManager worldManager){
        worldManager.getModuleCreatorRegistry().register(Key.key("overworld"), new CruxWorldModuleCreator() {
            @Override
            public @NotNull WorldModule create(@NotNull CruxWorld world) {
                return new USurviveOverworld(world);
            }
        });
        var customNether = new CruxWorldModuleCreator() {
            @Override
            public @NotNull WorldModule create(@NotNull CruxWorld world) {
                return new USurviveNether(world);
            }
        };
        worldManager.getModuleCreatorRegistry().register(Key.key("the_nether"), customNether);

        Key resourceOverworld = Key.key("world_resource_overworld");
        Key resourceNether = Key.key("world_resource_nether");

        worldManager.getModuleCreatorRegistry().register(
            resourceOverworld,
            new CruxWorldModuleCreator() {
                @Override
                public @NotNull WorldModule create(@NotNull CruxWorld world) {
                    return new ResetWorldModule(world, () -> values().RESOURCE_OVERWORLD_RESET_TIME(), USurviveWorldTypes.RESOURCE_OVERWORLD,
                        Map.of(resourceNether, USurviveWorldTypes.RESOURCE_NETHER));
                }
            },
            new CruxWorldModuleCreator() {
                @Override
                public @NotNull WorldModule create(@NotNull CruxWorld world) {
                    return new GenericOverworldModule(world);
                }
            },
            new CruxWorldModuleCreator() {
                @Override
                public @NotNull WorldModule create(@NotNull CruxWorld world) {
                    return new OverworldMobSpawnsModule(world);
                }
            },
            new CruxWorldModuleCreator(){
                @NotNull
                @Override
                public WorldModule create(@NotNull CruxWorld world) {
                    return new ResourceWorldModule(world, USurvivePlugin.this,
                        new Pair<>(resourceNether, USurviveWorldTypes.RESOURCE_NETHER),
                        null);
                }
            }
        );

        worldManager.getModuleCreatorRegistry().register(
            resourceNether,
            customNether,
            new CruxWorldModuleCreator(){
                @NotNull
                @Override
                public WorldModule create(@NotNull CruxWorld world) {
                    return new ResourceWorldModule(world, USurvivePlugin.this,
                        new Pair<>(resourceOverworld, USurviveWorldTypes.RESOURCE_OVERWORLD),
                        null);
                }
            }
        );
    }

    protected LangProvider langProvider;
    protected SpawnListener spawnListener;
    @Override
    public void enabled() {
        if(CruxRegistries.MODULES.containsKey(StandardModules.CRUX_CONFIGS)){
            values(new Config(this, "config"));
        }else{
            values(new DefaultValues());
        }
        cfgContainer = new UCfgContainer(this);

        USurviveBlocks.register();
        USurviveItems.register();

        if(CruxRegistries.MODULES.containsKey(StandardModules.CRUX_CONFIGS)){
            langProvider = new SimpleLangConfig(this, "lang", this::lang, Lang.class);
        }else{
            langProvider = this;
            LangPopulator.populate(lang, Msg.class);
        }

        registerListeners(
            (Listener) deathManager,
            this,
            new GrappleListener(),
            new QuickAccessListener(),
            new ObjectiveProgressListener(this),
            spawnListener = new SpawnListener(values, CruxCore.core(), playerManager),
            new CommandListener(),
            new RespawnListener(this, values, playerSettingsManager),
            new SetupListener(values),
            new DisableEndListener(),
            new EnchantsListener(this),
            new AnimalsAndPotionInfusionListener(values),
            new ProjectileReflectListener(),
            new SettingsListener(),
            new MiscListener(),
            new BedListener(),
            new ArmorTrimListener(),
            new CustomObjectivesListener(),
            new MaintenanceListener(),
            new VanishListener(),
            devCommands,
            new VillagerTradeListener(),
            new CustomEntityListener(),
            new SporelinkListener(),
            //new StoryListener(values),
            new TipListener(playerSettingsManager, USurviveRegistries.TIPS),
            new EntityHiderListener(this),
            new GlowingInitListener(this),
            new EjectiveItemLootListener()
            //new WaxPaintingListener()
        );

        if(getServer().getPluginManager().getPlugin("CruxWarps") != null){
            registerListeners(
                new CruxWarpsListener()
            );
        }

        if(getServer().getPluginManager().getPlugin("CruxClaims") != null){
            registerListeners(
                new ClaimingListener()
            );
        }

        super.enabled();

        new USurviveCommands(this).registerHelp();

        //deathManager.load();

        cfgManagerLoader.load();
        globalManagerLoader.load();

        setupQuickShop();

        reloadConfig();
        MaintenanceListener.maintenance = getConfig().getBoolean("maintenance");
    }

    public void setupQuickShop(){
        UQuickShop.setup(this);
    }

    public void registerMenuModules(MenuRegistry menuRegistry, @NotNull FileMenuHolder<?> fileMenuHolder, @NotNull KeyedRegistry<MenuModuleBuilder> registry) {
        registry.register(new SimpleFilePagedCfg(fileMenuHolder, Crux.key("paged_player_deaths")) {
            @NotNull
            @Override
            public MenuModule parsePaged(@NotNull String id,
                                         @NotNull NumberProvider indexes,
                                         @Nullable String valuesFilter,
                                         @Nullable MenuItems valueItems,
                                         @Nullable MenuItems emptyItems) {
                return new SimplePagedMenuModule<PlayerDeath>(id, indexes, valuesFilter, valueItems, emptyItems, this) {
                    @Override
                    public @NotNull Holder<List<PlayerDeath>> getValues(@NotNull CfgMenu cfgMenu) {
                        Player player = cfgMenu.info().getOrThrow(Player.class);
                        return () -> {
                            List<PlayerDeath> list = new ArrayList<>(deathManager.getAllDeaths(player));
                            list.sort(Comparator.comparing(PlayerDeath::getTime));
                            return list;
                        };
                    }
                };
            }
        });
        registry.register(new SimpleFilePagedCfg(fileMenuHolder, Crux.key("paged/friends_and_party")) {
            @NotNull
            @Override
            public MenuModule parsePaged(@NotNull String id,
                                         @NotNull NumberProvider indexes,
                                         @Nullable String valuesFilter,
                                         @Nullable MenuItems valueItems,
                                         @Nullable MenuItems emptyItems) {
                return new SimplePagedMenuModule<OfflinePlayer>(id, indexes, valuesFilter, valueItems, emptyItems, this) {
                    @Override
                    public @NotNull Holder<List<OfflinePlayer>> getValues(@NotNull CfgMenu cfgMenu) {
                        Player player = cfgMenu.info().getOrDefault("target", Player.class, cfgMenu.info().get(Player.class));
                        UUID uuid = player.getUniqueId();
                        return () -> {
                            Collection<UUID> set = new HashSet<>();
                            List<OfflinePlayer> list = new ArrayList<>();
                            FriendsHolder friends = CruxFriendsPlugin.inst().getFriendsManager().getFriendsHolder(player);
                            friends.getFriends().forEach(friend ->{
                                list.add(getServer().getOfflinePlayer(friend.user().uuid()));
                                set.add(friend.user().uuid());
                            });
                            PartyHolder partyHolder = CruxPartyPlugin.inst().getPartyManager().getPartyHolder(player);
                            Party party = partyHolder.getParty();
                            if(party != null){
                                if(!party.owner().equals(uuid) && !set.contains(party.owner())){
                                    list.add(getServer().getOfflinePlayer(party.owner()));
                                    set.add(party.owner());
                                }
                                party.members().forEach(memberUUID ->{
                                    if(memberUUID.equals(uuid) || set.contains(memberUUID)) return;
                                    list.add(getServer().getOfflinePlayer(memberUUID));
                                    set.add(memberUUID);
                                });
                            }
                            return list;
                        };
                    }
                };
            }
        });
        registry.register(new SimpleFilePagedCfg(fileMenuHolder, Crux.key("paged/custom_enchants")) {
            @NotNull
            @Override
            public MenuModule parsePaged(@NotNull String id,
                                         @NotNull NumberProvider indexes,
                                         @Nullable String valuesFilter,
                                         @Nullable MenuItems valueItems,
                                         @Nullable MenuItems emptyItems) {
                return new SimplePagedMenuModule<CruxEnchant>(id, indexes, valuesFilter, valueItems, emptyItems, this) {
                    @Override
                    public @NotNull Holder<List<CruxEnchant>> getValues(@NotNull CfgMenu cfgMenu) {
                        return () -> {
                            List<CruxEnchant> list = new ArrayList<>(CruxEnchantRegistries.CRUX_ENCHANT.values());
                            list.sort(Comparator.comparing(e -> e.key().value()));
                            return list;
                        };
                    }
                };
            }
        });

        registry.register(new SimpleFilePagedCfg(fileMenuHolder, Crux.key("paged/custom_potions")) {
            @NotNull
            @Override
            public MenuModule parsePaged(@NotNull String id,
                                         @NotNull NumberProvider indexes,
                                         @Nullable String valuesFilter,
                                         @Nullable MenuItems valueItems,
                                         @Nullable MenuItems emptyItems) {
                return new SimplePagedMenuModule<BrewingRecipeLoader.DisplayMix>(id, indexes, valuesFilter, valueItems, emptyItems, this) {
                    @Override
                    public @NotNull Holder<List<BrewingRecipeLoader.DisplayMix>> getValues(@NotNull CfgMenu cfgMenu) {
                        return () -> {
                            List<BrewingRecipeLoader.DisplayMix> list = new ArrayList<>(BrewingRecipeLoader.DISPLAY_POTION_MIXES.values());
                            list.sort(Comparator.comparing(mix -> mix.getCruxPotion().getName()));
                            return list;
                        };
                    }
                };
            }
        });

        registry.register(new SimpleFilePagedCfg(fileMenuHolder, Crux.key("paged/non_brew_custom_potions")) {
            @NotNull
            @Override
            public MenuModule parsePaged(@NotNull String id,
                                         @NotNull NumberProvider indexes,
                                         @Nullable String valuesFilter,
                                         @Nullable MenuItems valueItems,
                                         @Nullable MenuItems emptyItems) {
                return new SimplePagedMenuModule<BrewingRecipeLoader.DisplayMix>(id, indexes, valuesFilter, valueItems, emptyItems, this) {
                    @Override
                    public @NotNull Holder<List<BrewingRecipeLoader.DisplayMix>> getValues(@NotNull CfgMenu cfgMenu) {
                        return () -> {
                            List<BrewingRecipeLoader.DisplayMix> list = new ArrayList<>(BrewingRecipeLoader.NON_BREW_DISPLAY_POTION_MIXES.values());
                            list.sort(Comparator.comparing(mix -> mix.getCruxPotion().getName()));
                            return list;
                        };
                    }
                };
            }
        });

        menuRegistry.itemDataParsers().register(2, new USurviveDataParser(key("main")));
    }

    /*@EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        getServer().getScheduler().runTaskLater(this, task ->{
            if(!p.isOnline()) return;
            cfgManagerLoader.load(p);
        }, 20L);
    }*/

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        USurviveAdvancementLoaderHolder holder = EntityMemory.getOrCreateDataHolder(p, USurviveAdvancementLoaderHolder.class);
        if(holder != null && !holder.isLoaded()){
            return;
        }
        var mem = PlayerMemory.get(p);
        if(mem == null) return;
        mem.getPlayerDataHolderOrCompute(AdvancementSaverHolder.class, m -> new AdvancementSaverHolder(m, cfgManagerLoader, p));
        /*getServer().getScheduler().runTask(this, task ->{
            cfgManagerLoader.saveAndUnload(p);
        });*/
    }

    @Override
    public void disabled() {
        super.disabled();
        //deathManager.save();
        cfgManagerLoader.saveAndUnload(getServer().getOnlinePlayers());
        globalManagerLoader.saveAndUnload(getServer().getOnlinePlayers());
        if(spawnListener != null) spawnListener.onDisable();
    }

    @Override
    public void reload() {
        super.reload();
        new CfgLoader() {
            public void loadConfiguration(@NotNull DataFile cfg, @Nullable String path) {
                Dialoger table;
                if (path == null) {
                    table = cfg.deserialize("", Dialoger.class);
                } else {
                    FileElement var5 = cfg.getRoot();
                    if (!(var5 instanceof FileObject root)) {
                        return;
                    }

                    table = FileDialoger.FILE_DIALOGER.deserializeFromFile(new FileContext<>(cfg.fileRegistry()), root, Crux.key(path));
                }

                if (table instanceof KeyedDialoger keyed) {
                    Crux.log(Level.INFO, "Registered dialoger: " + keyed.key());
                    USurviveRegistries.DIALOGER.register(keyed);
                }
            }
        }.loadConfiguration(
            new CruxFolder(this, "npcs").file()
        );
        new CfgLoader() {
            public void loadConfiguration(@NotNull DataFile cfg, @Nullable String path) {
                KeyedDialogContent table;
                if (path == null) {
                    table = cfg.deserialize("", DialogContent.class);
                } else {
                    FileElement var5 = cfg.getRoot();
                    if (!(var5 instanceof FileObject root)) {
                        return;
                    }

                    table = (KeyedDialogContent) FileDialogContent.FILE_DIALOG_CONTENT.deserializeFromFile(new FileContext<>(cfg.fileRegistry()), root, Crux.key(path));
                }

                Crux.log(Level.INFO, "Registered dialog content: " + table.key());
                USurviveRegistries.DIALOG_CONTENT.register(table);
            }
        }.loadConfiguration(
            new CruxFolder(this, "dialog_content").file()
        );

        values.reload(this);
        langProvider.reload(this);

        cfgContainer.reload();

        CruxCore.inst().cruxMenus().menuRegistry().loadConfiguration(
            new CruxFolder(this, "menus").file()
        );

        cfgManagerLoader.refresh(manager ->{
            KillMobAdvanceMaker.registerAll();
        });
        globalManagerLoader.refresh();

        DataFile tipsFile = tipsFile();
        if(tipsFile != null){
            if(tipsFile.getElement("categories") instanceof FileObject o){
                var ctx = new FileContext<>(tipsFile.fileRegistry());
                o.forEach((key, element) ->{
                    Key finalKey = Crux.key(key);
                    TipCategory tipCategory = CfgHook.TIP_CATEGORY.deserializeFromFile(ctx, element, finalKey);
                    if(tipCategory == null || tipCategory.tips().isEmpty()) return;
                    USurviveRegistries.TIPS.register(tipCategory);
                });
            }
            tipsFile.close();
        }

        DataFile ranksFile = BukkitDataFile.parseFromGeneralPath(
            CruxFolder.file(this, "ranks"), false
        );
        if(ranksFile != null){
            if(ranksFile.getElement("values") instanceof FileObject o){
                var ctx = new FileContext<>(ranksFile.fileRegistry());
                o.forEach((key, ele) ->{
                    Rank rank = CfgHook.FILE_RANK.deserializeFromFile(ctx, ele, Crux.key(key), (FileObject) ranksFile.getRoot());
                    if(rank == null) return;
                    USurviveRegistries.RANK.register(rank);
                });
            }
            ranksFile.close();
        }
    }

    public DataFile tipsFile(){
        return BukkitDataFile.parseFromGeneralPath(CruxFolder.file(this, "tips.json"));
    }

    public void registerSimpleCfgHandlers(@NotNull FileRegistry registry){
        registry.registerFileHandler(PlayerSetting.class, new FilePlayerSetting());
        registry.registerFileHandler(PlayerSettingSelection.class, new FilePlayerSettingSelection());
        registry.registerFileHandler(SleepWeakness.class, new FileSleepWeakness());
        registry.registerFileHandler(SleepWeaknessCalculator.class, new FileSleepWeaknessCalculator());
        registry.registerFileHandler(SleepWeaknessWarning.class, new FileSleepWeaknessWarning());
        registry.registerFileHandler(RandomTPData.class, new FileRandomTPData());
        registry.registerFileHandler(RandomTPData.Pos.class, new FileRandomTPPos());
        registry.registerFileHandler(GuideProgression.class, CfgHook.FILE_GUIDE_PROGRESSION);
        registry.registerFileHandler(Rank.class, CfgHook.FILE_RANK);
        registry.registerFileHandler(StoryProgression.class, CfgHook.FILE_STORY_PROGRESSION);
        registry.registerFileHandler(StoryProgressData.class, CfgHook.FILE_STORY_PROGRESS_DATA);
        registry.registerFileHandler(
            new AutoFileHandler<>(PremiumRank.class),
            new AutoFileHandler<>(CfgLootData.class)
        );
        registry.registerFileHandler(GuidePoint.class, new FileGuidePoint());
        registry.registerFileHandler(DialogLine.class, new FileDialogLine());
        registry.registerFileHandler(DialogAction.class, new FileDialogAction());
        registry.registerFileHandler(DialogContent.class, FileDialogContent.FILE_DIALOG_CONTENT);
        registry.registerFileHandler(Dialoger.class, FileDialoger.FILE_DIALOGER);
        registry.registerFileHandler(
            StoryState.class,
            new FileStoryState()
        );
        registry.registerFileHandler(Tip.class, new FileTip());
        registry.registerFileHandler(TipCategory.class, CfgHook.TIP_CATEGORY);
    }

    public void registerCfgHandlers(@NotNull FileRegistry registry){
        registry.registerFileHandler(PlayerDeath.class, new FilePlayerDeath());
        AutoFileOptions cacheDisabled = AutoFileOptions.builder()
            .disabledFields(f -> f.getName().equalsIgnoreCase("cache"))
            .build();
        registry.registerFileHandler(
            new AutoFileHandler<>(ResourcePackData.class, cacheDisabled),
            new AutoFileHandler<>(ResourcePackRequestData.class, AutoFileOptions.builder()
                .disabledFields(f -> f.getName().equalsIgnoreCase("cache"))
                .manualHandlers(Map.of(
                    "resourcePacks", new FileConvertHandler<Collection<ResourcePackData>, Object>() {
                        @Override
                        public @NotNull FileElement serializeToFile(@NotNull FileContext<?> fileContext, @NotNull Collection<ResourcePackData> resourcePackData) {
                            return null;
                        }

                        @Override
                        public @Nullable Collection<ResourcePackData> deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
                            return ctx.getRegistry().deserializeFromFile(
                                new TypeToken<Collection<ResourcePackData>>(){}.getType(),
                                e
                            );
                        }

                        @Override
                        public Collection<ResourcePackData> convertTo(Object object) {
                            return List.of();
                        }

                        @Override
                        public Object convertFrom(Collection<ResourcePackData> resourcePackData) {
                            return null;
                        }
                    }
                ))
                .build())
        );
    }
}
