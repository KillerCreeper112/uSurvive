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
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.entity.memory.EntityMemory;
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
import killercreepr.cruxteleport.core.teleport.module.TeleportModules;
import killercreepr.usurvive.api.tpa.TPAHolder;
import killercreepr.usurvive.api.tpa.TPAManager;
import killercreepr.usurvive.api.tpa.TPARequest;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.command.argument.TargetPlayerDeathResolver;
import killercreepr.usurvive.core.command.argument.USurviveArgs;
import killercreepr.usurvive.core.death.PlayerDeath;
import killercreepr.usurvive.core.lang.Lang;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TPACommands {
    protected final @NotNull USurvivePlugin plugin;
    public TPACommands(@NotNull USurvivePlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            commands.register(
                Commands.literal("tpa")
                    .requires(source -> getExecutor(source).hasPermission("usurvive.cmds.tpa.use"))
                    .then(
                        Commands.argument("target", ArgumentTypes.player())
                            .executes(ctx -> {
                                if(!(getExecutor(ctx.getSource()) instanceof Entity sender)) return -1;
                                Player target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class)
                                    .resolve(ctx.getSource()).getFirst();
                                if(target.equals(sender)){
                                    return -1;
                                }

                                return 1;
                            })
                    )
                    .build()
            );

            commands.register(
                Commands.literal("tpaccept")
                    .requires(source -> getExecutor(source).hasPermission("usurvive.cmds.tpaccept.use"))
                    .executes(ctx ->{
                        if(!(getExecutor(ctx.getSource()) instanceof Entity sender)) return -1;

                        return 1;
                    })
                    .then(
                        Commands.argument("request")
                            .executes(ctx ->{
                                if(!(getExecutor(ctx.getSource()) instanceof Entity sender)) return -1;

                                return acceptTPA(sender, ctx.getArgument("request", ));//todo
                            })
                    )
                    .build()
            );
        });
    }

    public int acceptTPA(Entity sender, TPARequest request){

        if(CruxTeleporter.teleporter().hasScheduledTeleport(request.from())){
            return 0;//todo
        }

        TPAManager tpManager = plugin.getTPAManager();
        TPAHolder holder = tpManager.getTPAHolder(sender);
        if(holder == null){
            return 0;
        }
        TPARequest removed = holder.removeRequest(request.from());
        if(removed == null){
            return 0;
        }

        tpManager.acceptTPA(sender, removed);
        return 1;
    }

    public LiteralCommandNode<CommandSourceStack> build(LiteralArgumentBuilder<CommandSourceStack> dispatcher,
                                                        LifecycleEventManager<?> manager){
        return dispatcher.build();
    }

    public @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source){
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }
}
