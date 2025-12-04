package killercreepr.usurvive.core.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.parser.TextDataComponentDecoder;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.api.dialog.KeyedDialogContent;
import killercreepr.usurvive.api.dialog.listener.DialogListener;
import killercreepr.usurvive.api.story.StoryHolder;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.command.argument.USurviveArgs;
import killercreepr.usurvive.core.lang.Lang;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import static io.lumine.mythic.bukkit.utils.text.Text.DefaultFontInfo.p;

public class AdminCommands {
    protected final @NotNull USurvivePlugin plugin;
    public AdminCommands(@NotNull USurvivePlugin plugin) {
        this.plugin = plugin;
    }
    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            commands.register(
                Commands.literal("udialog")
                    .requires(ctx -> ctx.getSender().hasPermission("usurvive.command.udialog"))
                    .then(
                        Commands.literal("set")
                            .then(
                                Commands.argument("targets", ArgumentTypes.entities())
                                    .then(
                                        Commands.argument("dialog", USurviveArgs.DIALOG_CONTENT)
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var targets = ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
                                                    .resolve(ctx.getSource());
                                                var dialog = ctx.getArgument("dialog", KeyedDialogContent.class);

                                                for (var target : targets) {
                                                    var listener = DialogListener.dialogListener(target);
                                                    if(listener == null) continue;
                                                    listener.dialogPlayer().setDialog(dialog);
                                                    sender.sendMessage("Set dialog for " + target.getName() + " to " + dialog.key());
                                                }
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .then(
                        Commands.literal("queue")
                            .then(
                                Commands.argument("targets", ArgumentTypes.entities())
                                    .then(
                                        Commands.argument("dialog", USurviveArgs.DIALOG_CONTENT)
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var targets = ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
                                                    .resolve(ctx.getSource());
                                                var dialog = ctx.getArgument("dialog", KeyedDialogContent.class);

                                                for (var target : targets) {
                                                    var listener = DialogListener.dialogListener(target);
                                                    if(listener == null) continue;
                                                    listener.dialogPlayer().queueDialog(dialog);
                                                    sender.sendMessage("Queued dialog for " + target.getName() + " to " + dialog.key());
                                                }
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .then(
                        Commands.literal("push")
                            .then(
                                Commands.argument("targets", ArgumentTypes.entities())
                                    .then(
                                        Commands.argument("dialog", USurviveArgs.DIALOG_CONTENT)
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var targets = ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
                                                    .resolve(ctx.getSource());
                                                var dialog = ctx.getArgument("dialog", KeyedDialogContent.class);

                                                for (var target : targets) {
                                                    var listener = DialogListener.dialogListener(target);
                                                    if(listener == null) continue;
                                                    listener.dialogPlayer().pushDialog(dialog);
                                                    sender.sendMessage("Pushed dialog to " + target.getName() + ": " + dialog.key());
                                                }
                                                return 1;
                                            })
                                            .then(
                                                Commands.argument("force", BoolArgumentType.bool())
                                                    .executes(ctx ->{
                                                        var sender = getExecutor(ctx.getSource());
                                                        var targets = ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
                                                            .resolve(ctx.getSource());
                                                        var dialog = ctx.getArgument("dialog", KeyedDialogContent.class);
                                                        boolean force = ctx.getArgument("force", Boolean.class);

                                                        for (var target : targets) {
                                                            var listener = DialogListener.dialogListener(target);
                                                            if(listener == null) continue;
                                                            if(force){
                                                                listener.dialogPlayer().forcePushDialog(dialog);
                                                                sender.sendMessage("Force pushed dialog to " + target.getName() + ": " + dialog.key());
                                                            } else{
                                                                listener.dialogPlayer().pushDialog(dialog);
                                                                sender.sendMessage("Pushed dialog to " + target.getName() + ": " + dialog.key());
                                                            }
                                                        }
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                    )
                    .build()
            );

            //id=blaze,title="Blaze Buster",material=blaze_rod,amounts=[10, 25, 50, 100, 250, 500, 1000],levels=7
            //manager="usurvive:combat"
            commands.register(
                Commands.literal("uban")
                    .requires(ctx -> ctx.getSender().hasPermission("usurvive.command.uban"))
                    .then(
                        Commands.argument("targets", ArgumentTypes.players())
                            .then(
                                Commands.argument("reason", StringArgumentType.greedyString())
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                            .resolve(ctx.getSource());
                                        String reason = ctx.getArgument("reason", String.class);
                                        for(var p : targets){
                                            p.ban(reason, (Date) null, sender.getName(), true);
                                            Bukkit.broadcast(Crux.format().deserialize(
                                                "<red>" + sender.getName() + " <white>banned <red><player_name> <white>for: <red>" + reason,
                                                TagContainer.merged().hook(p)
                                            ));
                                        }
                                        return 1;
                                    })
                            )
                    )
                    .build()
            );
            commands.register(
                Commands.literal("utempban")
                    .requires(ctx -> ctx.getSender().hasPermission("usurvive.command.utempban"))
                    .then(
                        Commands.argument("targets", ArgumentTypes.players())
                            .then(
                                Commands.argument("duration", ArgumentTypes.time())
                                    .then(
                                        Commands.argument("reason", StringArgumentType.greedyString())
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                    .resolve(ctx.getSource());
                                                String reason = ctx.getArgument("reason", String.class);
                                                int ticksDuration = ctx.getArgument("duration", Integer.class);

                                                for(var p : targets){
                                                    p.ban(reason, Instant.ofEpochMilli(ticksDuration * 50L), sender.getName(), true);
                                                    Bukkit.broadcast(Crux.format().deserialize(
                                                        "<red>" + sender.getName() + " <white>banned <red><player_name> <white>for: <red>" + reason,
                                                        TagContainer.merged().hook(p)
                                                    ));
                                                }
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build()
            );
            commands.register(
                Commands.literal("ukick")
                    .requires(ctx -> ctx.getSender().hasPermission("usurvive.command.ukick"))
                    .then(
                        Commands.argument("targets", ArgumentTypes.players())
                            .then(
                                Commands.argument("reason", StringArgumentType.greedyString())
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                            .resolve(ctx.getSource());
                                        String reason = ctx.getArgument("reason", String.class);
                                        for(var p : targets){
                                            p.kick(Crux.format().deserialize(reason, TagContainer.merged().hook(p)));
                                            Bukkit.broadcast(Crux.format().deserialize(
                                                "<red>" + sender.getName() + " <yellow>banned <red><player_name> <yellow>for: <red>" + reason,
                                                TagContainer.merged().hook(p)
                                            ));
                                        }
                                        return 1;
                                    })
                            )
                    )
                    .build()
            );
            commands.register(
                Commands.literal("ustory")
                    .requires(ctx -> ctx.getSender().hasPermission("usurvive.command.ustory"))
                    .then(
                        Commands.literal("interact")
                            .then(
                                Commands.argument("targets", ArgumentTypes.players())
                                    .then(
                                        Commands.argument("data", StringArgumentType.greedyString())
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                    .resolve(ctx.getSource());
                                                String rawData = ctx.getArgument("data", String.class);
                                                DataExchange info = DataExchange.builder()
                                                    .edit(builder ->{
                                                        TextDataComponentDecoder.parseComponentMap(rawData).forEach(builder::put);
                                                    }).build();
                                                targets.forEach(p ->{
                                                    StoryHolder holder = StoryHolder.storyHolder(p);
                                                    if(holder == null) return;
                                                    holder.forEachCurrentStoryProgression(prog ->{
                                                        prog.onInfoInteraction(info);
                                                    });
                                                    if(!(sender instanceof ConsoleCommandSender))
                                                        sender.sendMessage("Sent interaction to " + p.getName() + "'s current story progressions.");
                                                });
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build()
            );
            commands.register(
                Commands.literal("utip")
                    .requires(ctx -> ctx.getSender().hasPermission("usurvive.command.utip"))
                    .then(
                        Commands.literal("show")
                            .then(
                                Commands.argument("targets", ArgumentTypes.players())
                                    .then(
                                        Commands.literal("random")
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                    .resolve(ctx.getSource());
                                                for (Player target : targets) {
                                                    var tip = USurviveRegistries.TIPS.randomTip();
                                                    if(tip == null) continue;
                                                    sender.sendMessage("Sent random tip message to " + target.getName());
                                                    Lang.TIP_MESSAGE.use(target, TagContainer.merged().hook(p).add(Tag.parsed("tip_message", tip.message())));
                                                }
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build()
            );
        });
    }

    public @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source){
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }
}
