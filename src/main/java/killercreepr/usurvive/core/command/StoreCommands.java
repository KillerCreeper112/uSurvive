package killercreepr.usurvive.core.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.usurvive.api.entity.player.ShopSpender;
import killercreepr.usurvive.api.event.PlayerPremiumPurchaseEvent;
import killercreepr.usurvive.core.USurvivePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StoreCommands {
    protected final @NotNull USurvivePlugin plugin;
    public StoreCommands(@NotNull USurvivePlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            commands.register(
                Commands.literal("premiumshop")
                    .requires(source -> getExecutor(source).hasPermission("usurvive.cmds.premiumshop.use"))
                    .then(
                        Commands.literal("purchased")
                            .then(
                                Commands.argument("player", ArgumentTypes.player())
                                    .then(
                                        Commands.argument("spent", DoubleArgumentType.doubleArg())
                                            .then(
                                                Commands.argument("reason", StringArgumentType.string())
                                                    .executes(ctx ->{
                                                        Player p = ctx.getArgument("player",
                                                            PlayerSelectorArgumentResolver.class)
                                                            .resolve(ctx.getSource()).getFirst();
                                                        double spent = ctx.getArgument("spent", Double.class);
                                                        String reason = ctx.getArgument("reason", String.class);

                                                        ShopSpender spender = ShopSpender.shopSpender(p);
                                                        spender.onPurchase(spent, reason);
                                                        new PlayerPremiumPurchaseEvent(p, spent, reason).callEvent();
                                                        getExecutor(ctx.getSource())
                                                            .sendMessage(p.getName() + " purchased an item. Spent: " + spent + ". Reason: " + reason);
                                                        spender.save();
                                                        return 1;
                                                    })
                                            )
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
