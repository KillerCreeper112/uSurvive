package killercreepr.usurvive;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.communication.Communicator;
import killercreepr.crux.api.communication.lang.CreateLang;
import killercreepr.crux.api.communication.lang.LangProvider;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.communication.MsgContainer;
import killercreepr.crux.core.communication.lang.LangPopulator;
import killercreepr.crux.core.communication.lang.Msg;
import killercreepr.crux.core.communication.lang.SimpleCreateLang;
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
import killercreepr.cruxitems.api.item.plugin.PluginItem;
import killercreepr.cruxitems.core.config.CruxItemsConfigHook;
import killercreepr.cruxitems.core.config.handler.FilePluginItemHandler;
import killercreepr.cruxmenus.CruxMenusModule;
import killercreepr.cruxmenus.api.menu.CfgMenu;
import killercreepr.cruxmenus.api.menu.config.handler.FileMenuHolder;
import killercreepr.cruxmenus.api.menu.holder.MenuItems;
import killercreepr.cruxmenus.api.menu.module.MenuModule;
import killercreepr.cruxmenus.api.menu.module.config.MenuModuleBuilder;
import killercreepr.cruxmenus.core.menu.module.standard.SimpleFilePagedCfg;
import killercreepr.cruxmenus.core.menu.module.standard.SimplePagedMenuModule;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.creator.CruxWorldModuleCreator;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.api.world.module.WorldModule;
import killercreepr.usurvive.block.USurviveBlocks;
import killercreepr.usurvive.block.component.USurviveBlockComponents;
import killercreepr.usurvive.command.USurviveCommands;
import killercreepr.usurvive.config.Config;
import killercreepr.usurvive.config.component.CfgUSurviveComponents;
import killercreepr.usurvive.config.handler.FilePlayerDeath;
import killercreepr.usurvive.core.component.USurviveComponent;
import killercreepr.usurvive.core.entity.player.settings.StandardPlayerSettings;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import killercreepr.usurvive.data.ResourcePackData;
import killercreepr.usurvive.data.ResourcePackRequestData;
import killercreepr.usurvive.data.entity.DeathHolder;
import killercreepr.usurvive.death.DeathManager;
import killercreepr.usurvive.death.PlayerDeath;
import killercreepr.usurvive.entity.USurviveMobs;
import killercreepr.usurvive.item.QuickAccessItem;
import killercreepr.usurvive.item.USurviveItems;
import killercreepr.usurvive.lang.Lang;
import killercreepr.usurvive.listener.*;
import killercreepr.usurvive.mechanics.grapple.standard.StandardGrappleHooks;
import killercreepr.usurvive.menu.module.CurrencyDepositModule;
import killercreepr.usurvive.tags.object.PlayerDeathTags;
import killercreepr.usurvive.values.DefaultValues;
import killercreepr.usurvive.values.ValuesProvider;
import killercreepr.usurvive.world.USurviveNether;
import killercreepr.usurvive.world.USurviveOverworld;
import killercreepr.usurvive.world.entity.StandardOverworldGroups;
import net.kyori.adventure.key.Key;
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

    protected final DeathManager deathManager = new DeathManager(this);
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

    protected final CfgCrazyAdvancementManagerCfgLoader cfgManagerLoader = new CfgCrazyAdvancementManagerCfgLoader(this);
    @Override
    public void onLoad() {
        instance = this;
        super.onLoad();
        USurviveComponent.register();
        StandardPlayerSettings.register();
        new USurviveCommands(this).register();

        CfgRegistries.FILE.forEach(this::registerCfgHandlers);
        CfgUSurviveComponents.register(BukkitCfgHandlers.TYPED_DATA_COMPONENT.typeHandlers());

        USurviveBlockComponents.register(BukkitCfgHandlers.TYPED_DATA_COMPONENT.typeHandlers());

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

        EntityMemory.registerFunction(this, mem ->{
            if(!(mem instanceof PlayerMemory m)) return;
            m.getDataHolders().register(new DeathHolder(m, values(), getDeathManager()));
        });
        StandardGrappleHooks.register(USurviveRegistries.GRAPPLE_HOOK);
        StandardOverworldGroups.register(USurviveRegistries.OVERWORLD_NATURAL_ENTITY_SPAWN_GROUP);

        CruxWorldManager worldManager = CruxCore.inst().worldManager();
        registerWorldData(worldManager);

        /*CruxWorldManager worldManager = CruxCore.inst().worldManager();
        worldManager.getCreatorRegistry().register("world", USurviveOverworld::new);
        worldManager.getCreatorRegistry().register("world_nether", USurviveNether::new);*/
        Crux.tags().register(new PlayerDeathTags());
    }

    public void registerWorldData(CruxWorldManager worldManager){
        worldManager.getModuleCreatorRegistry().register("world", new CruxWorldModuleCreator() {
            @Override
            public @NotNull WorldModule create(@NotNull CruxWorld world) {
                return new USurviveOverworld(world);
            }
        });
        worldManager.getModuleCreatorRegistry().register("world_nether", new CruxWorldModuleCreator() {
            @Override
            public @NotNull WorldModule create(@NotNull CruxWorld world) {
                return new USurviveNether(world);
            }
        });
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
            deathManager,
            this,
            new GrappleListener(),
            new QuickAccessListener(),
            new ObjectiveProgressListener(this),
            spawnListener = new SpawnListener(values, this),
            new CommandListener(),
            new RespawnListener(this, values),
            new SetupListener(values),
            new DisableEndListener(),
            new EnchantsListener(this),
            new AnimalsListener(),
            new ProjectileReflectListener()
        );

        super.enabled();

        deathManager.load();

        //cfgManagerLoader.load();
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
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        getServer().getScheduler().runTaskLater(this, task ->{
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
        deathManager.save();
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

        cfgManagerLoader.refresh();
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
