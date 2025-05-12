package killercreepr.usurvive.core;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.communication.Communicator;
import killercreepr.crux.api.communication.lang.CreateLang;
import killercreepr.crux.api.communication.lang.LangProvider;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.api.registry.Registry;
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
import killercreepr.cruxadvancements.core.config.loader.CfgCrazyAdvancementManagerCfgLoader;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.bukkit.standard.SimpleLangConfig;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.AutoFileOptions;
import killercreepr.cruxconfig.config.common.handler.FileConvertHandler;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxcore.recipes.BrewingRecipeLoader;
import killercreepr.cruxenchants.api.enchant.CruxEnchant;
import killercreepr.cruxenchants.core.registries.CruxEnchantRegistries;
import killercreepr.cruxfriends.api.friend.FriendsHolder;
import killercreepr.cruxfriends.core.CruxFriendsPlugin;
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.cruxitems.core.config.CruxItemsConfigHook;
import killercreepr.cruxitems.core.config.handler.FilePluginItemHandler;
import killercreepr.cruxmenus.CruxMenusModule;
import killercreepr.cruxmenus.api.menu.CfgMenu;
import killercreepr.cruxmenus.api.menu.action.MenuAction;
import killercreepr.cruxmenus.api.menu.config.handler.FileMenuHolder;
import killercreepr.cruxmenus.api.menu.holder.MenuItems;
import killercreepr.cruxmenus.api.menu.module.MenuModule;
import killercreepr.cruxmenus.api.menu.module.config.MenuModuleBuilder;
import killercreepr.cruxmenus.core.menu.module.standard.SimpleFilePagedCfg;
import killercreepr.cruxmenus.core.menu.module.standard.SimplePagedMenuModule;
import killercreepr.cruxparty.api.party.Party;
import killercreepr.cruxparty.api.party.PartyHolder;
import killercreepr.cruxparty.core.CruxPartyPlugin;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.creator.CruxWorldModuleCreator;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.api.world.module.WorldModule;
import killercreepr.cruxworlds.core.registries.WorldsRegistries;
import killercreepr.usurvive.api.death.DeathManager;
import killercreepr.usurvive.api.entity.player.UPlayerManager;
import killercreepr.usurvive.api.entity.player.data.RandomTPData;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsManager;
import killercreepr.usurvive.api.tpa.TPAManager;
import killercreepr.usurvive.api.values.SleeperCfg;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.advancement.auto.KillMobAdvanceMaker;
import killercreepr.usurvive.core.attribute.USurviveAttribute;
import killercreepr.usurvive.core.block.USurviveBlocks;
import killercreepr.usurvive.core.block.component.USurviveBlockComponents;
import killercreepr.usurvive.core.charm.CharmTypes;
import killercreepr.usurvive.core.command.DevCommands;
import killercreepr.usurvive.core.command.StoreCommands;
import killercreepr.usurvive.core.command.TPACommands;
import killercreepr.usurvive.core.command.USurviveCommands;
import killercreepr.usurvive.core.component.USurviveComponents;
import killercreepr.usurvive.core.config.CfgHook;
import killercreepr.usurvive.core.config.Config;
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
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.entity.memory.EntityInfusedPotionsHolder;
import killercreepr.usurvive.core.entity.memory.SleeperHolder;
import killercreepr.usurvive.core.entity.mob.USurviveMobCategory;
import killercreepr.usurvive.core.entity.player.SimpleUPlayerManager;
import killercreepr.usurvive.core.entity.player.settings.SimplePlayerSettingsManager;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettings;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeakness;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessCalculator;
import killercreepr.usurvive.core.entity.sleeper.effects.SleepWeaknessWarning;
import killercreepr.usurvive.core.entity.tickable.USurviveTickables;
import killercreepr.usurvive.core.item.QuickAccessItem;
import killercreepr.usurvive.core.item.USurviveItems;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.listener.*;
import killercreepr.usurvive.core.mechanics.grapple.standard.StandardGrappleHooks;
import killercreepr.usurvive.core.mechanics.whip.standard.StandardWhipHooks;
import killercreepr.usurvive.core.menu.action.CancelTPAAction;
import killercreepr.usurvive.core.menu.action.ConfirmTPAAction;
import killercreepr.usurvive.core.menu.action.PlayerSettingAction;
import killercreepr.usurvive.core.menu.action.PlayerSettingCacheAction;
import killercreepr.usurvive.core.menu.module.CurrencyDepositModule;
import killercreepr.usurvive.core.potion.USurvivePotions;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import killercreepr.usurvive.core.stat.USurviveStats;
import killercreepr.usurvive.core.statistic.USurviveStatistics;
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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class USurvivePlugin extends CruxPlugin implements Listener, LangProvider {
    private static USurvivePlugin instance;
    public static USurvivePlugin inst(){ return instance; }

    protected DeathManager deathManager;
    public DeathManager getDeathManager() {
        return deathManager;
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
    @Override
    public void onLoad() {
        instance = this;
        super.onLoad();
        deathManager = new SimpleDeathManager(this, CruxCore.core().worldManager());
        USurviveMobCategory.register();
        USurviveComponents.register();
        StandardPlayerSettings.register();
        USurviveStats.register();
        USurviveStatistics.register();
        USurviveAttribute.register();
        USurviveTickables.register();
        new USurviveCommands(this).register();
        new StoreCommands(this).register();
        new TPACommands(this).register();
        new DevCommands(this).register();
        USurvivePotions.register();

        CfgHook.load();
        CfgRegistries.FILE.forEach(this::registerCfgHandlers);
        CfgRegistries.SIMPLE_REGISTRY.forEach(this::registerSimpleCfgHandlers);
        CfgUSurviveComponents.register(BukkitCfgHandlers.TYPED_DATA_COMPONENT.typeHandlers());

        USurviveBlockComponents.register(BukkitCfgHandlers.TYPED_DATA_COMPONENT.typeHandlers());

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
        CruxMenusModule menusModule = CruxCore.inst().cruxMenus();
        registerMenuModules(menusModule.menuRegistry().menuModule(), menusModule.menuModuleRegistry());
        Registry<MenuAction> menuActions = menusModule.menuRegistry().menuActions();
        menuActions.register(new PlayerSettingCacheAction(Crux.key("player_settings_cache")));
        menuActions.register(new PlayerSettingAction(Crux.key("player_settings")));
        menuActions.register(new ConfirmTPAAction(Crux.key("confirm_tpa")));
        menuActions.register(new CancelTPAAction(Crux.key("cancel_tpa")));

        EntityMemory.registerFunction(this, mem ->{
            if(!(mem instanceof PlayerMemory m)) return;
            m.getDataHolders().register(new DeathHolder(m, values(), getDeathManager()));
            m.getDataHolders().register(new MemoryPlayerSettingsHolder(m));
            m.getDataHolders().register(new EntityInfusedPotionsHolder(m));
            m.getDataHolders().register(new SleeperHolder(m, () -> (SleeperCfg) values()));
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
            new SleeperHolderTags()
        ));
        if(getServer().getPluginManager().getPlugin("CruxClaims") != null){
            Crux.tags().register(new ClaimProfileTags());
        }
        Crux.format().globalStringResolvers().register(new USurviveTag());
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

        USurviveBlocks.register();
        USurviveItems.register();
        USurviveMobs.register();

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
            new RespawnListener(this, values),
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
            new VanishListener()
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

        setupQuickShop();

        reloadConfig();
        MaintenanceListener.maintenance = getConfig().getBoolean("maintenance");
    }

    public void setupQuickShop(){
        UQuickShop.setup(this);
    }

    public void registerMenuModules(@NotNull FileMenuHolder<?> fileMenuHolder, @NotNull KeyedRegistry<MenuModuleBuilder> registry) {
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
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        getServer().getScheduler().runTaskLater(this, task ->{
            if(!p.isOnline()) return;
            cfgManagerLoader.load(p);
        }, 20L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        getServer().getScheduler().runTask(this, task ->{
            cfgManagerLoader.saveAndUnload(p);
        });
    }

    @Override
    public void disabled() {
        super.disabled();
        //deathManager.save();
        cfgManagerLoader.saveAndUnload(getServer().getOnlinePlayers());
        if(spawnListener != null) spawnListener.onDisable();
    }

    @Override
    public void reload() {
        super.reload();
        values.reload(this);
        langProvider.reload(this);

        CruxCore.inst().cruxMenus().menuRegistry().loadConfiguration(
            new CruxFolder(this, "menus").file()
        );

        cfgManagerLoader.refresh(manager ->{
            KillMobAdvanceMaker.registerAll();
        });
    }

    public void registerSimpleCfgHandlers(@NotNull FileRegistry registry){
        registry.registerFileHandler(PlayerSetting.class, new FilePlayerSetting());
        registry.registerFileHandler(PlayerSettingSelection.class, new FilePlayerSettingSelection());
        registry.registerFileHandler(SleepWeakness.class, new FileSleepWeakness());
        registry.registerFileHandler(SleepWeaknessCalculator.class, new FileSleepWeaknessCalculator());
        registry.registerFileHandler(SleepWeaknessWarning.class, new FileSleepWeaknessWarning());
        registry.registerFileHandler(RandomTPData.class, new FileRandomTPData());
        registry.registerFileHandler(RandomTPData.Pos.class, new FileRandomTPPos());
        registry.registerFileHandler(
            new AutoFileHandler<>(PremiumRank.class),
            new AutoFileHandler<>(CfgLootData.class)
        );
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
