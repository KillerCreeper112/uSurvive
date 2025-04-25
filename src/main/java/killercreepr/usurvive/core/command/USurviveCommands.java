package killercreepr.usurvive.core.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.api.communication.Communicator;
import killercreepr.crux.api.component.parser.DataComponentDecoder;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.api.text.tags.TagsPrefixBuilder;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.cruxclaims.api.claiming.Claimer;
import killercreepr.cruxclaims.core.CruxClaims;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxcurrency.core.entity.memory.CurrencyHolder;
import killercreepr.cruxteleport.api.teleport.CruxTeleport;
import killercreepr.cruxteleport.api.teleport.CruxTeleporter;
import killercreepr.cruxteleport.api.teleport.world.RandomWorldTP;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.command.argument.TargetPlayerDeathResolver;
import killercreepr.usurvive.core.command.argument.USurviveArgs;
import killercreepr.usurvive.core.config.Config;
import killercreepr.usurvive.core.death.PlayerDeath;
import killercreepr.usurvive.core.lang.Lang;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class USurviveCommands {
    protected final @NotNull USurvivePlugin plugin;
    public USurviveCommands(@NotNull USurvivePlugin plugin) {
        this.plugin = plugin;
    }

    public void registerHelp(){
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->{
            final Commands commands = event.registrar();

            var cmd = Commands.literal("help")
                .executes(ctx ->{
                    if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                    CruxCore.inst().cruxMenus().menuRegistry().menuHolders().get(Crux.key("guide"))
                        .open(p);
                    return 1;
                });

            if(plugin.values() instanceof Config cfg){
                cfg.HELP_CMDS.valueOr(Map.of()).keySet().forEach(id ->{
                    cmd.then(
                        Commands.literal(id)
                            .executes(ctx ->{
                                if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                                var cmds = cfg.HELP_CMDS.valueOr(Map.of()).get(id);
                                if(cmds != null){
                                    cmds.forEach(s ->{
                                        s = Crux.format().deserializeString(s, TagContainer.merged().hook(p));
                                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), s);
                                    });
                                }
                                return 1;
                            })
                    );
                });
            }

            commands.register(
                cmd.build(), List.of("guide", "wiki", "faq", "support", "info", "tips", "hints")
            );
        });
    }

    public void register(){
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->{
            final Commands commands = event.registrar();
            LiteralCommandNode<CommandSourceStack> cmd = Commands.literal("spawn")
                .requires(source -> source.getSender().hasPermission("usurvive.cmds.spawn.use"))
                .executes(ctx ->{
                    if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                    ValuesProvider cfg = plugin.values();
                    String worldName = cfg.SPAWN_WORLD().value();
                    if(worldName == null) return 0;
                    World world = plugin.getServer().getWorld(worldName);
                    if(world == null){
                        Communicator.chat("<red>World not found.").use(p);
                        return 0;
                    }
                    CruxTeleport.Builder tp = CruxTeleport.builder()
                        .teleportTo(world::getSpawnLocation);

                    String modules = cfg.SPAWN_TELEPORT_COMPONENTS().value();
                    if(modules != null && !p.getWorld().equals(world)){
                        DataComponentDecoder.componentDecoder().parseComponents(modules).forEach(tp::set);
                        //tp.addModules(TeleportModules.parseFromString(modules, p.getUniqueId()));
                    }
                    /*tp.addModule(TeleportModules.msgBuilder()
                            .successMsg(Lang.DEFAULT_TELEPORT_SUCCESS_MSG)
                            .failedMsg(Lang.DEFAULT_TELEPORT_FAIL_MSG)
                            .tickMsg(Lang.DEFAULT_TELEPORT_TICK_MSG)
                            .msgInterval(cfg.SPAWN_TELEPORT_MSG_INTERVAL().value().intValue())
                        .build());*/

                    CruxTeleporter.teleporter().scheduleTeleport(p, tp.build());
                    return 1;
                }).build();
            commands.register(cmd, List.of("s"));

            commands.register(
                Commands.literal("quickaccess")
                    .requires(source -> source.getSender().hasPermission("usurvive.cmds.quickaccess.use"))
                    .executes(ctx ->{
                        if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                        CruxCore.inst().cruxMenus().menuRegistry().menuHolders().get(Crux.key("quick_access"))
                            .open(p);
                        return 1;
                    })
                    .then(
                        Commands.argument("targets", ArgumentTypes.players())
                            .requires(source -> source.getSender().hasPermission("usurvive.cmds.quickaccess.admin"))
                            .executes(ctx ->{
                                Collection<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                    .resolve(ctx.getSource());
                                for(Player p : targets){
                                    CruxCore.inst().cruxMenus().menuRegistry().menuHolders().get(Crux.key("quick_access"))
                                        .open(p);
                                }
                                getExecutor(ctx.getSource()).sendMessage("Opened quick access menu for " + targets.size() + " players.");
                                return 1;
                            })
                    )
                    .build(),
                List.of("menu", "usurvive", "quickmenu", "fastaccess")
            );

            commands.register(
                Commands.literal("store")
                    .executes(ctx ->{
                        if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                        CruxCore.inst().cruxMenus().menuRegistry().menuHolders().get(Crux.key("premium/store_mockup"))
                            .open(p);
                        return 1;
                    })
                    .build(), List.of("webstore", "website")
            );

            commands.register(
                Commands.literal("settings")
                    .requires(source -> source.getSender().hasPermission("usurvive.cmds.settings.use"))
                    .executes(ctx ->{
                        if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                        CruxCore.inst().cruxMenus().menuRegistry().menuHolders().get(Crux.key("player_settings"))
                            .open(p);
                        return 1;
                    })
                    .build(), List.of("psettings", "options")
            );

            commands.register(
                Commands.literal("recipes")
                    .requires(source -> source.getSender().hasPermission("usurvive.cmds.recipes.use"))
                    .executes(ctx ->{
                        if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                        CruxCore.inst().cruxMenus().menuRegistry().menuHolders().get(Crux.key("crafting/standard/recipe_list"))
                            .open(p);
                        return 1;
                    })
                    .build(), List.of("crafting", "craft")
            );

            commands.register(
                Commands.literal("rules")
                    .executes(ctx ->{
                        if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;

                        ItemStack book = DynamicItem.builder()
                            .material("crux:rules").build()
                            .buildItem(TextParserContext.empty());
                        if(book == null || !(book.getItemMeta() instanceof BookMeta meta)){
                            CruxCore.inst().cruxMenus().menuRegistry().menuHolders().get(Crux.key("guide_rules"))
                                .open(p);
                            return 1;
                        }
                        p.openBook(meta);
                        return 1;
                    })
                    .build(), List.of("guidelines", "tos", "terms")
            );

            commands.register(
                Commands.literal("discord")
                    .executes(ctx ->{
                        Lang.DISCORD.use(getExecutor(ctx.getSource()));
                        return 1;
                    })
                    .build(), List.of("socials", "social")
            );

            commands.register(
                Commands.literal("orbits")
                    .executes(ctx ->{
                        if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                        CurrencyHolder holder = EntityMemory.getOrCreateDataHolder(p, CurrencyHolder.class);
                        if(holder == null){
                            Communicator.chat("<red>Error. Contact developer.").use(p);
                            return 0;
                        }
                        double amount = holder.getBank().getCurrency(Crux.key("orbit"));
                        Lang.CHECK_ORBITS.use(p, TagContainer.merged().hook(p).add(Tag.parsed("orbits", amount + "")));
                        return 1;
                    })
                    .build(), List.of("orbit", "balance", "bal", "nova", "novas")
            );

            commands.register(
                Commands.literal("profile")
                    .requires(source -> source.getSender().hasPermission("usurvive.cmds.profile.use"))
                    .executes(ctx ->{
                        if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                        CruxCore.inst().cruxMenus().menuRegistry().menuHolders().get(Crux.key("profile"))
                            .open(p, DataExchange.empty(), TagContainer.merged().hook(p, TagsPrefixBuilder.overwriteBase("target_")));
                        return 1;
                    })
                    .then(
                        Commands.argument("target", StringArgumentType.string())
                            .suggests((ctx, builder) ->{
                                plugin.getServer().getOnlinePlayers().forEach(p ->{
                                    builder.suggest(p.getName());
                                });
                                return builder.buildFuture();
                            })
                            .executes(ctx ->{
                                if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;

                                String input = ctx.getArgument("target", String.class);
                                Claimer claimer = CruxClaims.inst().claimManager().getClaimers()
                                    .stream()
                                    .filter(c -> c.lastKnownName().equalsIgnoreCase(input))
                                    .findFirst()
                                    .orElse(null);

                                if(claimer == null){
                                    Communicator.chat("<red>Player " + input + " not found.");
                                    return -1;
                                }

                                OfflinePlayer target = plugin.getServer().getOfflinePlayer(claimer.uuid());
                                /*Player target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class)
                                        .resolve(ctx.getSource()).getFirst();*/
                                CruxCore.inst().cruxMenus().menuRegistry().menuHolders().get(Crux.key("profile"))
                                    .open(p, DataExchange.empty(), TagContainer.merged().hook(target, TagsPrefixBuilder.overwriteBase("target_")));
                                return 1;
                            })
                    )
                    .build(), List.of("player", "account")
            );

            commands.register(
                Commands.literal("usurvive")
                    .requires(sender -> sender.getSender().hasPermission("usurvive.cmds.usurvive.use"))
                    .then(
                        Commands.literal("death")
                            .then(
                                Commands.argument("target", ArgumentTypes.player())
                                    .then(
                                        Commands.argument("death", USurviveArgs.TARGET_PLAYER_DEATH)
                                            .then(
                                                Commands.literal("tp")
                                                    .executes(ctx ->{
                                                        Player target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class)
                                                            .resolve(ctx.getSource()).getFirst();
                                                        return tpToDeath(
                                                            ctx.getSource(),
                                                            target,
                                                            ctx.getArgument("death", TargetPlayerDeathResolver.class)
                                                                .resolve(ctx.getSource(), target),
                                                            Set.of(target)
                                                        );
                                                    })
                                                    .then(
                                                        Commands.argument("to_teleport", ArgumentTypes.players())
                                                            .executes(ctx ->{
                                                                Player target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class)
                                                                    .resolve(ctx.getSource()).getFirst();
                                                                return tpToDeath(
                                                                    ctx.getSource(),
                                                                    target,
                                                                    ctx.getArgument("death", TargetPlayerDeathResolver.class)
                                                                        .resolve(ctx.getSource(), target),
                                                                    ctx.getArgument("to_teleport", PlayerSelectorArgumentResolver.class)
                                                                        .resolve(ctx.getSource())
                                                                );
                                                            })
                                                    )
                                            )
                                    )
                            )
                    )
                    .build(), List.of("usu")
            );
        });
    }

    public LiteralCommandNode<CommandSourceStack> build(LiteralArgumentBuilder<CommandSourceStack> dispatcher,
                                                        LifecycleEventManager<?> manager){
        return dispatcher.build();
    }

    public static @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source){
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }

    public int tpToDeath(CommandSourceStack source, Player target, PlayerDeath death, Collection<Player> toTp){
        CommandSender sender = getExecutor(source);
        World world = death.getWorld();
        if(world == null){
            sender.sendMessage("World not found for " + target.getName() + "'s death at " + death.getTime() + ".");
            return 0;
        }
        Location spawn = death.getPosition().toLocation(world);
        for(Player p : toTp){
            //RandomWorldTP.tpNear(spawn, 16).findLocationToTeleportAsync(p)
            CruxTeleport tp = CruxTeleport.teleport(
                DataComponentDecoder.componentDecoder().parseComponents("[teleport/disable_commands={}]"),
                RandomWorldTP.centerNear(spawn, NumberProvider.uniform(16, 40))
            );
            CruxTeleporter.teleporter().scheduleTeleport(p, tp);
            sender.sendMessage("Scheduled teleport for " + p.getName() + " at " + target.getName() + "'s death in world, " + world.getName() + ": " +
                spawn.getBlockX() + ", " + spawn.getBlockY() + ", " + spawn.getBlockZ() + ".");
        }
        return 1;
    }
}
