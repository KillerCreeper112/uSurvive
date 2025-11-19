package killercreepr.usurvive.core.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.parser.TextDataComponentDecoder;
import killercreepr.usurvive.api.story.StoryHolder;
import killercreepr.usurvive.core.USurvivePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class AdminCommands {
    protected final @NotNull USurvivePlugin plugin;
    public AdminCommands(@NotNull USurvivePlugin plugin) {
        this.plugin = plugin;
    }
    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            //id=blaze,title="Blaze Buster",material=blaze_rod,amounts=[10, 25, 50, 100, 250, 500, 1000],levels=7
            //manager="usurvive:combat"
            commands.register(
                Commands.literal("uban")
                    .requires(ctx -> ctx.getSender().hasPermission("usurvive.cmds.uban.use"))
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
                    .requires(ctx -> ctx.getSender().hasPermission("usurvive.cmds.utempban.use"))
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
                    .requires(ctx -> ctx.getSender().hasPermission("usurvive.cmds.ukick.use"))
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
                    .requires(ctx -> ctx.getSender().hasPermission("usurvive.cmds.ustory.use"))
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
                                                    sender.sendMessage("Sent interaction to " + p.getName() + "'s current story progressions.");
                                                });
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
