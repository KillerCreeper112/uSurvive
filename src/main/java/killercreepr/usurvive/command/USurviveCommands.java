package killercreepr.usurvive.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.data.communication.Communicator;
import killercreepr.cruxteleport.teleport.CruxTeleport;
import killercreepr.cruxteleport.teleport.module.TeleportModules;
import killercreepr.cruxteleport.util.CruxTeleporter;
import killercreepr.usurvive.USurvivePlugin;
import killercreepr.usurvive.lang.Lang;
import killercreepr.usurvive.values.ValuesProvider;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class USurviveCommands {
    protected final @NotNull USurvivePlugin plugin;
    public USurviveCommands(@NotNull USurvivePlugin plugin) {
        this.plugin = plugin;
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
                        .teleportTo(world::getSpawnLocation)
                        .waitingTime(cfg.SPAWN_TELEPORT_TIME().value().intValue());

                    String modules = cfg.SPAWN_TELEPORT_MODULES().value();
                    if(modules != null){
                        tp.addModules(TeleportModules.parseFromString(modules, p.getUniqueId()));
                    }
                    tp.addModule(TeleportModules.msgBuilder()
                            .successMsg(Lang.DEFAULT_TELEPORT_SUCCESS_MSG)
                            .failedMsg(Lang.DEFAULT_TELEPORT_FAIL_MSG)
                            .tickMsg(Lang.DEFAULT_TELEPORT_TICK_MSG)
                        .build());

                    CruxTeleporter.teleporter().scheduleTeleport(p, tp.build());
                    return 1;
                }).build();
            commands.register(cmd, List.of("s"));
        });
    }

    public LiteralCommandNode<CommandSourceStack> build(LiteralArgumentBuilder<CommandSourceStack> dispatcher,
                                                        LifecycleEventManager<?> manager){
        return dispatcher.build();
    }

    public @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source){
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }
}
